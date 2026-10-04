package application;

public class CursorNode {
	private Object element;
	private int next;

	public CursorNode(Object element, int next) {
		this.element = element;
		this.next = next;
	}

	public Object getElement() {
		return element;
	}

	public void setElement(Object element) {
		this.element = element;
	}

	public int getNext() {
		return next;
	}

	public void setNext(int next) {
		this.next = next;
	}
}
