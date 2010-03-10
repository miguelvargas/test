package com.mvw.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.mvw.command.TestCommand;
import com.mvw.command.TestCommand2;
import com.mvw.event.CommandEvent;


public class CommandControllerTest {

	public static boolean commandRan = false;
	public static boolean commandRan2 = false;
	
	public static Object lock = new Object();
	public static AtomicInteger threadCount = new AtomicInteger();
	
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
		commandRan = false;
		commandRan2 = false;
		
		CommandController controller = new CommandController();
		TestEvent tEvent = new TestEvent();
		controller.addCommand(TestCommand.class, tEvent);
		
		// check before command has run
		assertFalse(commandRan);
		try {
			synchronized (lock) {
				controller.dispatchEvent(tEvent);
				lock.wait(); // wait for command to finish				
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(commandRan);
		
	}
	
	
	@Test
	public void testControllerAddTwoCommand() {
		commandRan = false;
		commandRan2 = false;
		CommandController controller = new CommandController();
		
		TestEvent tEvent = new TestEvent();
		controller.addCommand(TestCommand.class, tEvent);
		
		controller.addCommand(TestCommand2.class, tEvent);
		
		// check before command has run
		assertFalse(commandRan);
		assertFalse(commandRan2);
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
		
		assertTrue(commandRan);
		assertTrue(commandRan2);
	}
	
	@Test
	public void testControllerRemoveCommand() {
		commandRan = false;
		commandRan2 = false;
		CommandController controller = new CommandController();
		TestEvent tEvent = new TestEvent();
		controller.addCommand(TestCommand.class, tEvent);
		
		
		controller.addCommand(TestCommand2.class, tEvent);
		
		// check before command has run
		assertFalse(commandRan);
		assertFalse(commandRan2);
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
		assertTrue(commandRan);
		assertTrue(commandRan2);
		
		controller.removeCommand(TestCommand.class, tEvent);
		// now only cmd2 should run, as we have removed cmd
		commandRan = false;
		commandRan2 = false;
		threadCount.set(1);
		controller.dispatchEvent(tEvent);
		try {
			 // wait for the 1 thread to run
			Thread.sleep(200); // wait some additional time, in case thread 2 did run (it should not)
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		
		assertFalse("command ran, but was removed, bug",commandRan);
		assertTrue(commandRan2);
		
		controller.removeCommand(TestCommand2.class, tEvent);
		// remove the final command
		commandRan = false;
		commandRan2 = false;
		
		controller.dispatchEvent(tEvent);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertFalse(commandRan);
		assertFalse(commandRan2);
		
	}
	
	@Test
	public void TestSyncEvent() {
		commandRan = false;
		commandRan2 = false;
		CommandController controller = new CommandController();
		TestSyncEvent tEvent = new TestSyncEvent();
		controller.addCommand(TestCommand.class, tEvent);
		
		controller.dispatchEvent(tEvent);
		assertTrue(commandRan); // should be true if sync
		
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
