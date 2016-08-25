package com.zlz.bug.test;

public class TestSuperClass {

	public static void main(String[] args) {
		// Father a = new Father();
		// Chilren b = new Chilren();
		Father c = new Chilren();
		// a.getAge();
		// System.out.println(a.age);
		// b.getAge();
		// System.out.println(b.age);
		c.getAge();
		System.out.println(c.age);
	}
}

class Father {

	public Father() {
		System.out.println(this.toString());
		System.out.println("父类");
	}

	int age = 40;

	public void getAge() {

		System.out.println(age);
	}
}

class Chilren extends Father {
	public Chilren() {
		System.out.println(this.toString());
		System.out.println("子类");
	}

	int age = 18;

	public void getAge() {
		System.out.println(age);
	}
}