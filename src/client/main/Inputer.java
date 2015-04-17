package client.main;
import lib.Json;
import lib.event.EventSystem;
import lib.event.EventSystem.Executable;
import lib.event.MiniEvent;
import all.EE;
import client.info.UserInfo;
import client.messenger.MessengerUser;

import com.alibaba.fastjson.JSONObject;


/*
 * This is the middle object for dealing the user input text
 * 
 * */
public class Inputer {

	private String mActionMsgPattern = "^:\\w+ ?.+$";
	private String PREFIX_GLOBAL = "[from system]";
	private String PREFIX = "[app not yet started]\n<> ";
	private String PREFIX_COMMAND = "<command> ";
	private String mCommandSpliter = " ";
	private boolean isStarted = false;

	private Manager mManager;
	private MessengerUser mMessengerUser;
	private InputerActioner mInputerActioner;
	private EventSystem mEventSystem;
	private MiniEvent mClientEvent;
	private UserInfo mUserInfo;

	public Inputer(Manager manager) {
		initParams(manager);
		initEvents();
	}
	
	private void initParams(Manager manager) {
		mManager = manager;
		mEventSystem = EventSystem.getSingleInstance();
		mClientEvent = (MiniEvent) mManager.getModule("miniEvent");
		mMessengerUser = (MessengerUser) mManager.getModule("messengerUser");
		mUserInfo = (UserInfo) mManager.getModule("userInfo");
		mInputerActioner = new InputerActioner(manager);
	}
	
	private void initEvents() {
		mEventSystem.register(EE.client_input, onInput);
		mClientEvent.register(EE.actionUser_newUser_done, onNewUserDone);
		mClientEvent.register(EE.client_start, onStart);
	}
	

	/* events */

	private Executable onInput = new Executable() {
		public void execute(Object data) {
			dealMessage((String) data);
		}
	};

	private MiniEvent.Executable onNewUserDone = new MiniEvent.Executable() {
		public void execute(Object data) {
			setupUser();
		}
	};
	
	private MiniEvent.Executable onStart = new MiniEvent.Executable() {
		public void execute(Object data) {
			isStarted = true;
			mClientEvent.trigger(EE.client_output, "[now started.]");
		}
	};

	/* actions */
	
	public void setupUser() {
		String name = mUserInfo.getUserInfo().getString("name");
		PREFIX = "<" + name + "> ";
	}

	private void dealMessage(String msg) {
		if (isCommand(msg)) {
			
			String name = getCommandName(msg);
			JSONObject dict = Json.create(
				"action", name,
				"content", getCommandContent(msg),
				"return", false
			);
			boolean isExist = mInputerActioner.execute(name, dict);
			if (!isExist) {
				mEventSystem.trigger(EE.client_output, getPrefixCommand() + "'" + name + "' not exist.");
			}
		}
		else if (isStarted) {
			mMessengerUser.addMessage(buildMessage(msg));
			mEventSystem.trigger(EE.client_output, getPrefix() + msg);
		}
		else {
			mEventSystem.trigger(EE.client_output, getPrefix() + msg);
		}
	}
	
	/* helpers */

	private boolean isCommand(String msg) {
		if (msg == null) { return false; }
		return msg.matches(mActionMsgPattern);
	}
	
	private String getCommandName(String msg) {
		int i = msg.indexOf(mCommandSpliter);
		i = (i > -1) ? i : msg.length(); 
		return msg.substring(1, i);
	}
	
	private String getCommandContent(String msg) {
		int i = msg.indexOf(mCommandSpliter);
		i = (i > -1) ? i : msg.length() - 1;
		return msg.substring(i + 1, msg.length());
	}
	
	private String getPrefixCommand() {
		return PREFIX_GLOBAL + PREFIX_COMMAND;
	}
	
	private String getPrefix() {
		return PREFIX_GLOBAL + PREFIX;
	}

	/* message manipulate functions */

	private JSONObject buildMessage(String msg) {
		return mMessengerUser.makeMessage("message", Json.create(
			"message", msg
		));
	}
}







