package io.github.future0923.debug.power.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author future0923
 */
public class DebugPowerJsonUtilsTest {

    @Test
    public void queryConvertDebugPowerJson() {
        Assertions.assertNotEquals(DebugPowerJsonUtils.queryConvertDebugPowerJson("https://www.iconfont.cn/api/common/suggest.json?q=import&type=icon&t=1715302971995&ctoken=TSzvd6zR0QMhoi2wCCSC5xNm"), "{}");
        Assertions.assertNotEquals(DebugPowerJsonUtils.queryConvertDebugPowerJson("?q=import&type=icon&t=1715302971995&ctoken=TSzvd6zR0QMhoi2wCCSC5xNm"), "{}");
        Assertions.assertNotEquals(DebugPowerJsonUtils.queryConvertDebugPowerJson("q=import&type=icon&t=1715302971995&ctoken=TSzvd6zR0QMhoi2wCCSC5xNm"), "{}");
    }
}