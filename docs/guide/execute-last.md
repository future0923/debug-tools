# 快捷调用上一次 {#execute-last}

DebugTools 的 `Quick Debug` 快捷调用方法是通过所在鼠标位置来确认要调用的类和方法的，当我们通过断点调用时方法调用已经在别的方法时，我们要想在重新调用一次就需要找到刚才的起始处重新唤醒 `Quick Debug`，这样非常的不方法。  

所以这时我们可以唤醒右键，点击 `Execute Last` 按钮，这样不管你鼠标当前位置在哪我们都可以快速重新调用上一次的方法了。

![execute_last_menu](/images/execute_last_menu.png){v-zoom}

::: tip
重新调用上次方法时，**传递的参数完全跟上次一样**，包括参数、xxl-job、header、配置等所有数据。
:::