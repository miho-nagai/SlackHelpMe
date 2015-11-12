package slackhelpme.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import slackhelpme.Activator;

// ウィンドウ＞設定＞SlackHelpMeで設定できる値の管理
public class SlackHelpMePreferenceInitializer extends AbstractPreferenceInitializer {

    // token
    public static final String KEY_TOKEN   = "";

    public void initializeDefaultPreferences() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setDefault(KEY_TOKEN, "");
    }
}
