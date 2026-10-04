package application;

public class Stack {
	private StackAndQueueNode Front;
	private int size;

	Stack() {
		Front = null;
		size = 0;
	}

	public StackAndQueueNode getFront() {
		return Front;
	}

	public void setFront(StackAndQueueNode front) {
		Front = front;
	}

	public int getSize() {
		return size;
	}

	public boolean isEmpty() {
		return getFront() == null;
	}

	public void push(Object element) {
		StackAndQueueNode newNode = new StackAndQueueNode(element);
		newNode.setNext(getFront());
		setFront(newNode);
		size++;
	}

	public Object pop() {
		if (isEmpty()) {
			return null;
		} else {
			StackAndQueueNode temp = getFront();
			setFront(getFront().getNext());
			size--;
			return temp.getElement();
		}
	}

	public Object peek() {
		if (isEmpty()) {
			return null;
		}
		return getFront().getElement();
	}

	public void clear() {
		setFront(null);
		size = 0;
	}

}
