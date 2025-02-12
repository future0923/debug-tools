package io.github.future0923.debug.tools.hotswap.core.plugin.classes.dto;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class AnonymousClassInfos {

    private static final Logger LOGGER = Logger.getLogger(AnonymousClassInfos.class);

    public static final int UNIQUE_CLASS_START_INDEX = 10000;

    private static final long ALLOWED_MODIFICATION_DELTA = 100;

    static int uniqueClass = UNIQUE_CLASS_START_INDEX;

    AnonymousClassInfos previous;

    Map<AnonymousClassInfo, AnonymousClassInfo> compatibleTransitions;

    long lastModifiedTimestamp = 0;

    String className;

    List<AnonymousClassInfo> anonymousClassInfoList = new ArrayList<>();

    public AnonymousClassInfos(ClassLoader classLoader, String className) {
        this.className = className;

        try {
            // reflective call to check already loaded class (not to load a new one)
            Method m = ClassLoader.class.getDeclaredMethod("findLoadedClass", new Class[]{String.class});
            m.setAccessible(true);

            int i = 1;
            while (true) {
                Class anonymous = (Class) m.invoke(classLoader, className + "$" + i);
                if (anonymous == null) {
                    break;
                }
                anonymousClassInfoList.add(i - 1, new AnonymousClassInfo(anonymous));
                i++;
            }
        } catch (Exception e) {
            throw new Error("Unexpected error in checking loaded classes", e);
        }
    }

    public AnonymousClassInfos(ClassPool classPool, String className) {
        this.className = className;
        lastModifiedTimestamp = lastModified(classPool, className);
        List<CtClass> declaredClasses;
        try {
            CtClass ctClass = classPool.get(className);
            declaredClasses = Arrays.asList(ctClass.getNestedClasses());
        } catch (NotFoundException e) {
            throw new IllegalArgumentException("Class " + className + " not found.");
        }


        int i = 1;
        while (true) {
            try {
                CtClass anonymous = classPool.get(className + "$" + i);
                if (!declaredClasses.contains(anonymous))
                    break; // skip obsolete classes
                anonymousClassInfoList.add(i - 1, new AnonymousClassInfo(anonymous));
                i++;
            } catch (NotFoundException e) {
                break;
            } catch (Exception e) {
                throw new Error("Unable to create AnonymousClassInfo definition for class " + className + "$i", e);
            }
        }
        LOGGER.trace("Anonymous class '{}' scan finished with {} classes found", className, i - 1);
    }

    private void calculateCompatibleTransitions() {
        compatibleTransitions = new HashMap<>();
        List<AnonymousClassInfo> previousInfos = new ArrayList<>(previous.anonymousClassInfoList);
        List<AnonymousClassInfo> currentInfos = new ArrayList<>(anonymousClassInfoList);
        if (previousInfos.size() > currentInfos.size()) {
            if (currentInfos.size() == 0)
                previousInfos.clear();
            else
                previousInfos = previousInfos.subList(0, currentInfos.size());
        }

        searchForMappings(compatibleTransitions, previousInfos, currentInfos, new AnonymousClassInfoMatcher() {
            @Override
            public boolean match(AnonymousClassInfo previous, AnonymousClassInfo current) {
                return previous.matchExact(current);
            }
        });

        searchForMappings(compatibleTransitions, previousInfos, currentInfos, new AnonymousClassInfoMatcher() {
            @Override
            public boolean match(AnonymousClassInfo previous, AnonymousClassInfo current) {
                return previous.matchSignatures(current);
            }
        });

        searchForMappings(compatibleTransitions, previousInfos, currentInfos, new AnonymousClassInfoMatcher() {
            @Override
            public boolean match(AnonymousClassInfo previous, AnonymousClassInfo current) {
                return previous.matchClassSignature(current);
            }
        });


        int newDefinitionCount = anonymousClassInfoList.size();
        int lastAnonymousClassIndex = previous.anonymousClassInfoList.size();

        for (AnonymousClassInfo currentNotMatched : currentInfos) {
            if (lastAnonymousClassIndex < newDefinitionCount) {
                compatibleTransitions.put(new AnonymousClassInfo(className + "$" + (lastAnonymousClassIndex + 1)), currentNotMatched);
                lastAnonymousClassIndex++;
            } else {
                compatibleTransitions.put(new AnonymousClassInfo(className + "$" + uniqueClass++), currentNotMatched);
            }
        }

        if (LOGGER.isLevelEnabled(Logger.Level.TRACE)) {
            for (Map.Entry<AnonymousClassInfo, AnonymousClassInfo> mapping : compatibleTransitions.entrySet()) {
                LOGGER.trace("Transition {} => {}", mapping.getKey().getClassName(), mapping.getValue().getClassName());
            }
        }
    }

    private void searchForMappings(Map<AnonymousClassInfo, AnonymousClassInfo> transitions, List<AnonymousClassInfo> previousInfos, List<AnonymousClassInfo> currentInfos,
                                   AnonymousClassInfoMatcher matcher) {
        for (ListIterator<AnonymousClassInfo> previousIt = previousInfos.listIterator(); previousIt.hasNext(); ) {
            AnonymousClassInfo previous = previousIt.next();

            for (ListIterator<AnonymousClassInfo> currentIt = currentInfos.listIterator(); currentIt.hasNext(); ) {
                AnonymousClassInfo current = currentIt.next();
                if (matcher.match(previous, current)) {
                    transitions.put(previous, current);
                    previousIt.remove();
                    currentIt.remove();
                    break;
                }
            }
        }
    }

    public AnonymousClassInfo getAnonymousClassInfo(String className) {
        for (AnonymousClassInfo info : anonymousClassInfoList) {
            if (className.equals(info.getClassName())) {
                return info;
            }
        }
        return null;
    }

    public void mapPreviousState(AnonymousClassInfos previousAnonymousClassInfos) {
        this.previous = previousAnonymousClassInfos;
        previousAnonymousClassInfos.previous = null;
        calculateCompatibleTransitions();
    }

    public boolean isCurrent(ClassPool classPool) {
        return lastModifiedTimestamp >= lastModified(classPool, className) - ALLOWED_MODIFICATION_DELTA;
    }

    private long lastModified(ClassPool classPool, String className) {
        URL url = classPool.find(className);
        if (url != null) {
            String file = url.getFile();
            return new File(file).lastModified();
        }
        return 0;
    }

    // matcher helper
    private interface AnonymousClassInfoMatcher {
        public boolean match(AnonymousClassInfo previous, AnonymousClassInfo current);
    }

    public Map<AnonymousClassInfo, AnonymousClassInfo> getCompatibleTransitions() {
        return compatibleTransitions;
    }

    public String getCompatibleTransition(String className) {
        for (Map.Entry<AnonymousClassInfo, AnonymousClassInfo> transition : compatibleTransitions.entrySet()) {
            if (transition.getKey().getClassName().equals(className))
                return transition.getValue().getClassName();
        }

        return null;
    }

}
