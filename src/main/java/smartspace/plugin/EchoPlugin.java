package smartspace.plugin;

import org.springframework.stereotype.Component;

import smartspace.data.ActionEntity;

@Component
public class EchoPlugin implements Plugin{

	@Override
	public ActionEntity process(ActionEntity action) {
		return action;
	}

}
