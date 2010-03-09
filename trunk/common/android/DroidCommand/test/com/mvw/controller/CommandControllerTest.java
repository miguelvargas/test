package com.mvw.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.mvw.command.Command;
import com.mvw.event.CommandEvent;


public class CommandControllerTest {

	public Object lock = new Object();
	public AtomicInteger threadCount = new AtomicInteger();
	
	@Test
	public void testControllerSetup() {
		CommandController controller = new CommandController();
		assertNotNull(controller);
	}

	@Test
	public void testControllerCustomPool() {
		
		ExecutorService eService = Executors.newFixedThreadPool(10);
		CommandController controller = new CommandController(eService);
		assertNotNull(controller);
	}
	@Test
	public void testControllerAddCommand() {
		CommandController controller = new CommandController();
		TestCommand cmd = new TestCommand();
		TestEvent tEvent = new TestEvent();
		controller.addCommand(cmd, tEvent);
		
		// check before command has run
		assertFalse(cmd.commandRan);
		try {
			synchronized (lock) {
				controller.dispatchEvent(tEvent);
				lock.wait(); // wait for command to finish				
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(cmd.commandRan);
		
	}
	
	
	@Test
	public void testControllerAddTwoCommand() {
		CommandController controller = new CommandController();
		TestCommand cmd = new TestCommand();
		TestEvent tEvent = new TestEvent();
		controller.addCommand(cmd, tEvent);
		
		TestCommand cmd2 = new TestCommand();
		controller.addCommand(cmd2, tEvent);
		
		// check before command has run
		assertFalse(cmd.commandRan);
		assertFalse(cmd2.commandRan);
		threadCount.set(2);
		controller.dispatchEvent(tEvent);
		int i = 0;
		while(true) {
			try {
				synchronized (lock) {
					lock.wait(100); // wait for command to finish				
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (threadCount.get() == 0) {
				break;
			}
			if (i > 10) {
				assertFalse("Timeout reached while waiting for our commands to execute",true);
				break;
			}
			i++;
		}
		
		assertTrue(cmd.commandRan);
		assertTrue(cmd2.commandRan);
	}
	
	@Test
	public void testControllerRemoveCommand() {
		CommandController controller = new CommandController();
		TestCommand cmd = new TestCommand();
		TestEvent tEvent = new TestEvent();
		controller.addCommand(cmd, tEvent);
		
		TestCommand cmd2 = new TestCommand();
		controller.addCommand(cmd2, tEvent);
		
		// check before command has run
		assertFalse(cmd.commandRan);
		assertFalse(cmd2.commandRan);
		threadCount.set(2);
		controller.dispatchEvent(tEvent);
		int i = 0;
		while(true) {
			try {
				synchronized (lock) {
					lock.wait(100); // wait for command to finish				
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (threadCount.get() == 0) {
				break;
			}
			if (i > 10) {
				assertFalse("Timeout reached while waiting for our commands to execute",true);
				break;
			}
			i++;
		}
		assertTrue(cmd.commandRan);
		assertTrue(cmd2.commandRan);
		
		controller.removeCommand(cmd, tEvent);
		// now only cmd2 should run, as we have removed cmd
		cmd.commandRan = false;
		cmd2.commandRan = false;
		threadCount.set(1);
		controller.dispatchEvent(tEvent);
		try {
			 // wait for the 1 thread to run
			Thread.sleep(200); // wait some additional time, in case thread 2 did run (it should not)
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		
		assertFalse("command ran, but was removed, bug",cmd.commandRan);
		assertTrue(cmd2.commandRan);
		
		controller.removeCommand(cmd2, tEvent);
		// remove the final command
		cmd.commandRan = false;
		cmd2.commandRan = false;
		
		controller.dispatchEvent(tEvent);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertFalse(cmd.commandRan);
		assertFalse(cmd.commandRan);
		
	}
	
	@Test
	public void TestSyncEvent() {
		CommandController controller = new CommandController();
		TestCommand cmd = new TestCommand();
		TestSyncEvent tEvent = new TestSyncEvent();
		controller.addCommand(cmd, tEvent);
		
		controller.dispatchEvent(tEvent);
		assertTrue(cmd.commandRan); // should be true if sync
		
	}
	
	
	
	
	public class TestCommand implements Command {

		public boolean commandRan = false;
		
		@Override
		public Object execute(CommandEvent event) {
			synchronized (lock) {
					
				commandRan = true;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				threadCount.decrementAndGet();
				lock.notify();
			}
			return null;
		}
	
	}
	
	public class TestEvent extends CommandEvent {

		@Override
		public String getType() {
			return "TestEvent";
		}
	
	}
	
	public class TestSyncEvent extends CommandEvent {
		// make it sync event
		public TestSyncEvent () {
			super(SYNC_TYPE.Sync);
		}
		
		@Override
		public String getType() {
			return "SyncEvent";
		}
	
	}
	
}
