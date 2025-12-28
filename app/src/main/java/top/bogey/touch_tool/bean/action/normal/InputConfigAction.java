package top.bogey.touch_tool.bean.action.normal;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.parent.DynamicPinsAction;
import top.bogey.touch_tool.bean.action.parent.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.service.TaskRunnable;

public class InputConfigAction extends ExecuteAction implements DynamicPinsAction {

    public InputConfigAction() {
        super(ActionType.INPUT_CONFIG);
    }

    public InputConfigAction(JsonObject jsonObject) {
        super(jsonObject);
        tmpPins.forEach(this::addPin);
        tmpPins.clear();
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        List<Pin> dynamicPins = getDynamicPins();
    }

    @Override
    public List<Pin> getDynamicPins() {
        List<Pin> pins = new ArrayList<>();
        boolean start = false;
        for (Pin pin : getPins()) {
            if (start) pins.add(pin);
            if (pin == outPin) start = true;
        }
        return pins;
    }
}
