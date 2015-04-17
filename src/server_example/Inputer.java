package server_example;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import lib.Json;
import server.main.Manager;
import server.messager.MessengerUser;

import com.alibaba.fastjson.JSONObject;



public class Inputer extends Thread {

	private String mActionMsgPattern = "^:\\w+ .+$";
	private String PREFIX = "<Inputer says> ";

	private Manager mManager;
	private Outputer mOutputer;
	private MessengerUser mMessengerUser;
	private InputerActioner mInputerActioner;

	public Inputer(Manager manager) {
		mManager = manager;
		mMessengerUser = (MessengerUser) mManager.getModule("messengerUser");
		
		mInputerActioner = new InputerActioner(manager);
	}
	
	/* run */
	
	@Override
	public void run() {
		scanInput();
	}

	/* getter and setter */

	public void setOutputer(Outputer outputer) {
		mOutputer = outputer;
		mInputerActioner.setOutputer(outputer);
	}

	/* actions */

	public void scanInput() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String line;
		boolean flagExit;
		try {
			while (true) {
					line = br.readLine();
					flagExit = dealMessage(line);
					if (flagExit) {
						break;
					}
			}
			if (br != null) br.close();
		} catch (IOException e) { e.printStackTrace(); }
		
	}

	/* helpers */

	private boolean dealMessage(String msg) {
		if (isCommand(msg)) {
			
			String name = getCommandName(msg);
			JSONObject dict = Json.create(
				"action", name,
				"content", getCommandContent(msg),
				"return", false
			);
			mInputerActioner.execute(name, dict);

			return dict.getBoolean("return");
		}
		else {
			mMessengerUser.addMessage(buildMessage(msg));
			mOutputer.show(PREFIX + msg);
			return false;	
		}
	}

	private boolean isCommand(String msg) {
		return msg.matches(mActionMsgPattern);
	}
	
	private String getCommandName(String msg) {
		int i = msg.indexOf(" ");
		return msg.substring(1, i);
	}
	
	private String getCommandContent(String msg) {
		int i = msg.indexOf(" ");
		return msg.substring(i + 1, msg.length());
	}

	/* message manipulate functions */

	private JSONObject buildMessage(String msg) {
		return mMessengerUser.makeMessage("message", Json.create(
			"message", msg
		));
	}
}







