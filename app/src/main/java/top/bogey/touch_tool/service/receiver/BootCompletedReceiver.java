package top.bogey.touch_tool.service.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.ContextThemeWrapper;

import com.google.android.material.color.DynamicColors;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.save.SettingSaver;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.ui.custom.KeepAliveFloatView;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isEnabled()) {
                if (SettingSaver.getInstance().isBootCompletedAutoStart()) {
                    ContextThemeWrapper themeWrapper = new ContextThemeWrapper(context, R.style.Theme_TouchTool_DayNight);
                    Context themeContext = DynamicColors.wrapContextIfAvailable(themeWrapper, R.style.Theme_TouchTool_DayNight);
                    new KeepAliveFloatView(themeContext).show();
                }
            }
        }
    }
}
