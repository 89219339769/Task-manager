package manager;

class Node<Task> {
    private Node<Task> prev;
    private Node<Task> next;
    private Task task;

    Node(Node<Task> prev, Node<Task> next, Task task) {
        this.prev = prev;
        this.next = next;
        this.task = task;
    }

    Node<Task> getPrev() {
        return this.prev;
    }

    Node<Task> getNext() {
        return this.next;
    }

    tasks.Task getItem() {
        return (tasks.Task) this.task;
    }

    void setPrev(Node<Task> prev) {
        this.prev = prev;
    }

    void setNext(Node<Task> next) {
        this.next = next;
    }

    @Override
    public String toString() {
        return
                " task=" + task ;
    }
}
