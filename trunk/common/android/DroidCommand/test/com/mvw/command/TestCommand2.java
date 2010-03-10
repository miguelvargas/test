package com.mvw.command;

import com.mvw.controller.CommandControllerTest;
import com.mvw.event.CommandEvent;

public class TestCommand2 implements Command {

	
	@Override
	public void execute(CommandEvent event) {
		synchronized (CommandControllerTest.lock) {
				
			CommandControllerTest.commandRan2 = true;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			CommandControllerTest.threadCount.decrementAndGet();
			CommandControllerTest.lock.notify();
		}
		return;
	}

}
