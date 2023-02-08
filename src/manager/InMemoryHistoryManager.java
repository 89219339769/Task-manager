package manager;

import tasks.Task;


import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> head;
    private Node<Task> tail;
    public Map<Integer, Node<Task>> historyMap = new LinkedHashMap<>();

    //добавляем задачу в список просмотров
    @Override
    public void add(Task task) {

        if (task == null) {
            return;
        }
        if (historyMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        Node<Task> prev = tail;
        Node<Task> last = new Node<>(prev, null, task);
        tail = last;
        if (head == null) {
            head = last;
        }
        if (prev != null) {
            prev.setNext(last);
        }
        historyMap.put(task.getId(), tail);
    }

    //удаляем  задачу из списка просмотров
    @Override
    public void remove(int id) {
        removeNode(historyMap.get(id));
        historyMap.remove(id);
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            Node<Task> prevNode = node.getPrev();
            Node<Task> nextNode = node.getNext();
            if (prevNode != null) {
                prevNode.setNext(nextNode);
            } else {
                head = nextNode;
            }
            if (nextNode != null) {
                nextNode.setPrev(prevNode);
            } else {
                tail = prevNode;
            }
        }
    }

    //возвращаем список просмотров

    public List<Task> getHistory() {
        List<Task> finalListOfValue = new ArrayList<>();
        List<Node<Task>> listOfValue = new LinkedList<>(historyMap.values());

        for (int i = 0; i < listOfValue.size(); i++) {
            Node node = listOfValue.get(i);
            finalListOfValue.add(node.getItem());
        }
        return finalListOfValue;
    }

}







