package smartspace.plugin;

import smartspace.data.ActionEntity;

public interface Plugin {
	public ActionEntity process (ActionEntity action);
}
