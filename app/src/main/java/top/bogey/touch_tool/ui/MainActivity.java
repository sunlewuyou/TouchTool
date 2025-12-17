package top.bogey.touch_tool.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.save.SettingSaver;
import top.bogey.touch_tool.databinding.ActivityMainBinding;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.service.TaskInfoSummary;
import top.bogey.touch_tool.ui.custom.KeepAliveFloatView;
import top.bogey.touch_tool.ui.tool.task_manager.ImportTaskDialog;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;

    public static Fragment getCurrentFragment() {
        MainActivity activity = MainApplication.getInstance().getActivity();
        if (activity == null) return null;
        Fragment navFragment = activity.getSupportFragmentManager().getPrimaryNavigationFragment();
        if (navFragment == null || !navFragment.isAdded()) return null;
        return navFragment.getChildFragmentManager().getPrimaryNavigationFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().setActivity(this);
        TaskInfoSummary.getInstance().resetApps();

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        SettingSaver.getInstance().init(this);

        MainAccessibilityService.enabled.observe(this, enabled -> {
            if (MainApplication.getInstance().getService() == null) return;
            if (enabled) {
                View view = FloatWindow.getView(KeepAliveFloatView.class.getName());
                if (view != null) return;
                new KeepAliveFloatView(this).show();
            } else {
                FloatWindow.dismiss(KeepAliveFloatView.class.getName());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null && service.isEnabled()) {
            View view = FloatWindow.getView(KeepAliveFloatView.class.getName());
            if (view != null) return;
            new KeepAliveFloatView(this).show();
        }

        NavController controller = Navigation.findNavController(this, R.id.conView);
        NavigationUI.setupWithNavController(binding.menuView, controller);

        controller.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            int id = navDestination.getId();
            if (id == R.id.task || id == R.id.tool || id == R.id.setting) {
                showBottomNavigation();
            } else {
                hideBottomNavigation();
            }
        });

        String runningError = SettingSaver.getInstance().getRunningError();
        if (runningError != null && !runningError.isEmpty()) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.dialog_title)
                    .setMessage(R.string.running_error_tips)
                    .setPositiveButton(R.string.copy_and_join, (dialog, which) -> {
                        AppUtil.copyToClipboard(this, runningError, true);
                        AppUtil.gotoUrl(this, getString(R.string.setting_qq_group_url));
                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.copy, (dialog, which) -> {
                        AppUtil.copyToClipboard(this, runningError, true);
                        dialog.dismiss();
                    })
                    .setNeutralButton(R.string.cancel, null)
                    .show();
            SettingSaver.getInstance().setRunningError(null);
        }

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onNightModeChanged(int mode) {
        super.onNightModeChanged(mode);
        FloatWindow.dismiss(KeepAliveFloatView.class.getName());
    }

    public void showBottomNavigation() {
        binding.menuView.setVisibility(View.VISIBLE);
    }

    public void hideBottomNavigation() {
        binding.menuView.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController controller = Navigation.findNavController(this, R.id.conView);
        return controller.navigateUp() || super.onSupportNavigateUp();
    }

    private void handleIntent(Intent intent) {
        if (intent == null) return;
        setIntent(null);

        Uri uri = null;
        if (Intent.ACTION_SEND.equals(intent.getAction())) {
            uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            uri = intent.getData();
        }
        if (uri != null) {
            ImportTaskDialog.showDialog(this, uri);
        }
    }
}