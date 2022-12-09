package task_managers.history_managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    Map<Long, Node> taskIdsToNodes = new HashMap<>();
    private Node head;
    private Node tail;
    private int size;

    private Node linkLast(Task task) {
        Node oldTail = tail;
        Node newTail = new Node(task);
        newTail.setPrev(oldTail);

        tail = newTail;
        if (oldTail == null) {
            head = newTail;
        } else {
            oldTail.setNext(newTail);
        }
        size++;

        return newTail;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>(size);
        Node iter = tail;
        while (iter != null) {
            tasks.add(iter.getTask());
            iter = iter.getPrev();
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (size < 2) {
            head = null;
            tail = null;
            return;
        }

        Node prev = node.getPrev();
        Node next = node.getNext();

        if (node == head) head = next;
        if (node == tail) tail = prev;

        if (prev != null) prev.setNext(next);
        if (next != null) next.setPrev(prev);
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        // если эта задача уже есть в истории - удаляем и добавляем снова
        if (taskIdsToNodes.containsKey(task.getId())) {
            remove(task.getId());
        }
        Node newNode = linkLast(task);
        taskIdsToNodes.put(task.getId(),newNode);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(long id) {
        Node nodeToRemove = taskIdsToNodes.get(id);
        if (nodeToRemove == null) return;
        taskIdsToNodes.remove(id);
        removeNode(nodeToRemove);
    }
}