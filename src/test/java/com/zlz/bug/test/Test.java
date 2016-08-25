package com.zlz.bug.test;

public class Test {
	public static void main(String[] args) throws InterruptedException {
		final TestChild t = new TestChild();

		new Thread(new Runnable() {
			public void run() {
				t.doSomething();
			}
		}).start();
		Thread.sleep(100);
		t.doSomethingElse();
	}

	public synchronized void doSomething() {
		System.out.println("something sleepy!");
		try {
			Thread.sleep(1000);
			System.out.println("woke up!");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static class TestChild extends Test {
		public void doSomething() {
			super.doSomething();
		}

		public synchronized void doSomethingElse() {
			System.out.println("something else");
		}
	}
}