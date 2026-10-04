package application;

public class Queue {
	private StackAndQueueNode Front;
	private StackAndQueueNode rear;
	private int Size;

	Queue() {
		this.Front = null;
		this.rear = null;
		this.Size = 0;
	}

	public StackAndQueueNode getFront() {
		return Front;
	}

	public void setFront(StackAndQueueNode front) {
		Front = front;
	}

	public StackAndQueueNode getRear() {
		return rear;
	}

	public void setRear(StackAndQueueNode rear) {
		this.rear = rear;
	}

	public int getSize() {
		return Size;
	}

	public boolean isEmpty() {
		return getFront() == null;
	}

	public void enQueue(Object element) {
		StackAndQueueNode newNode = new StackAndQueueNode(element);

		if (getFront() == null) {
			setFront(newNode);
			setRear(newNode);
		} else {
			getRear().setNext(newNode);
			setRear(newNode);
		}
		Size++;
	}

	public Object deQueue() {
		if (isEmpty()) {
			return null;
		} else {
			StackAndQueueNode temp = getFront();
			setFront(getFront().getNext());
			Size--;
			if (getFront() == null) {
				setRear(null);
			}
			return temp.getElement();

		}
	}

	public boolean remove(Object element) {
		StackAndQueueNode previous = null;
		StackAndQueueNode current = getFront();
		while (current != null && current.getElement() != element) {
			previous = current;
			current = current.getNext();
		}
		if (current == null) {
			return false;
		}
		if (previous == null) {
			setFront(current.getNext());
		} else {
			previous.setNext(current.getNext());
		}
		if (current == getRear()) {
			setRear(previous);
		}
		Size--;
		return true;
	}

	public void clear() {
		setFront(null);
		setRear(null);
		Size = 0;
	}

	public Object peek() {
		if (isEmpty()) {
			return null;
		} else {
			return getFront().getElement();
		}
	}

}
