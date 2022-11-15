package tasks_managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
    //********************Tasks*****************************************************************************************
    List<Task> getAllTasks();

    Task getTaskById(long id);

    boolean addTask(Task newTask);

    boolean updateTask(Task updatedTask);

    void deleteTaskById(long id);

    void deleteAllTasks();
    //******************************************************************************************************************


    //********************Subtasks**************************************************************************************
    List<Subtask> getAllSubtasks();

    Subtask getSubtaskById(long id);

    boolean addSubtask(Subtask newSubtask);

    boolean updateSubtask(Subtask updatedSubtask);

    void deleteSubtaskById(long id);

    void deleteAllSubtasks();
    //******************************************************************************************************************


    //********************Epics*****************************************************************************************
    List<Epic> getAllEpics();

    Epic getEpicById(long id);

    List<Subtask> getEpicSubtasks(long epicId);

    boolean addEpic(Epic newEpic);

    boolean updateEpic(Epic updatedEpic);

    void deleteEpicById(long id);

    void deleteAllEpics();
    //******************************************************************************************************************
}