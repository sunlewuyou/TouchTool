// IYoloCallback.aidl
package top.bogey.yolo;

import top.bogey.touch_tool.service.YoloResult;

interface IYoloCallback {
    void onResult(in List<YoloResult> result);
}