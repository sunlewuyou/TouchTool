package top.bogey.touch_tool.ui.blueprint.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.LayoutInflater;

import androidx.recyclerview.widget.ItemTouchHelper;

import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.UUID;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.CardInputConfigBinding;
import top.bogey.touch_tool.ui.blueprint.pin.PinBottomView;
import top.bogey.touch_tool.ui.blueprint.pin.PinLeftView;
import top.bogey.touch_tool.ui.blueprint.pin.PinRightView;
import top.bogey.touch_tool.ui.blueprint.pin.PinTopView;
import top.bogey.touch_tool.ui.blueprint.pin.PinView;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.ui.DragViewHolderHelper;

@SuppressLint("ViewConstructor")
public class InputConfigActionCard extends ActionCard implements IDynamicPinCard {
    private CardInputConfigBinding binding;
    private InputConfigActionAdapter adapter;

    public InputConfigActionCard(Context context, Task task, Action action) {
        super(context, task, action);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void init() {
        adapter = new InputConfigActionAdapter(this);
        binding = CardInputConfigBinding.inflate(LayoutInflater.from(getContext()), this, true);

        DragViewHolderHelper helper = new DragViewHolderHelper(DragViewHolderHelper.VERTICAL, adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(helper);

        binding.inPinBox.setAdapter(adapter);
        touchHelper.attachToRecyclerView(binding.inPinBox);

        initCardInfo(binding.icon, binding.title, binding.des);
        initEditDesc(binding.editButton, binding.des);
        initDelete(binding.removeButton);
        initLock(binding.lockButton);
        initPosView(binding.position);

        binding.addButton.setOnClickListener(v -> {
            Pin inPin = new Pin(new PinString(), 0, false, true);
            Pin outPin = new Pin(new PinString(), 0, true, false);
            String uid = UUID.randomUUID().toString();
            inPin.setUid(uid);
            outPin.setUid(uid);
            action.addPin(inPin);
            action.addPin(outPin);
        });
    }

    public void swap(int from, int to) {

    }

    @Override
    public void refreshCardInfo() {
        initCardInfo(null, binding.title, binding.des);
    }

    @Override
    public void refreshCardLockState() {
        initLock(binding.lockButton);
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void addPinView(Pin pin, int offset) {
        PinView pinView;
        if (pin.isDynamic()) {
            pinView = adapter.addPin(pin);
        } else {
            if (pin.isOut()) {
                if (pin.isVertical()) {
                    pinView = new PinBottomView(getContext(), this, pin);
                    binding.bottomBox.addView(pinView, binding.bottomBox.getChildCount() - offset);
                } else {
                    pinView = new PinRightView(getContext(), this, pin);
                    binding.outBox.addView(pinView, binding.outBox.getChildCount() - offset);
                }
            } else {
                if (pin.isVertical()) {
                    pinView = new PinTopView(getContext(), this, pin);
                    binding.topBox.addView(pinView, binding.topBox.getChildCount() - offset);
                } else {
                    pinView = new PinLeftView(getContext(), this, pin);
                    binding.inBox.addView(pinView, binding.inBox.getChildCount() - offset);
                }
            }
        }
        pinView.expand(action.getExpandType());
        pinViews.put(pin.getId(), pinView);
    }

    @Override
    public void removePinView(Pin pin) {
        if (pin.isDynamic()) {
            adapter.removePin(pin);
        } else {
            super.removePinView(pin);
        }
    }

    @Override
    public boolean isEmptyPosition(float x, float y) {
        float scale = getScaleX();

        List<MaterialButton> buttons = List.of(binding.expandButton, binding.addButton, binding.lockButton, binding.copyButton, binding.removeButton, binding.editButton);
        for (MaterialButton button : buttons) {
            PointF pointF = DisplayUtil.getLocationRelativeToView(button, this);
            float px = pointF.x * scale;
            float py = pointF.y * scale;
            float width = button.getWidth() * scale;
            float height = button.getHeight() * scale;
            if (new RectF(px, py, px + width, py + height).contains(x, y)) return false;
        }
        return super.isEmptyPosition(x, y);
    }

    @Override
    public void suppressLayout() {
        binding.inPinBox.suppressLayout(true);
        postDelayed(() -> binding.inPinBox.suppressLayout(false), 100);
    }
}
