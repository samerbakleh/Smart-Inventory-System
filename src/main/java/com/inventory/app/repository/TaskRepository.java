package com.inventory.app.repository;

import com.inventory.app.model.ProductType;
import com.inventory.app.model.Task;
import com.inventory.app.model.enums.TaskStatus;


import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    private ArrayList <Task> tasks = new ArrayList<>();
    private ProductTypeRepository productTypeRepository;
    private final String TASK_FILE = "src/main/resources/data/tasks.csv";
    public TaskRepository() {}
    public void setProductTypeRepository(ProductTypeRepository productTypeRepository) {
        this.productTypeRepository = productTypeRepository;
    }

    public void save (Task task){

             tasks.add(task);
             System.out.println("saved");
             task.setSaved(true);

     }

     public ArrayList<Task> getTasks() {
         return tasks;
     }
     public List<ProductType>getProductTypes()
     {
         return productTypeRepository.getProductTypes();
     }

    public Task findById(int id) {
        for (Task task : tasks) {
            if (task.getTaskID() == id) {
                return task;
            }
        }
        return null;
    }
    public void saveTasks(){
         try (PrintWriter printWriter = new PrintWriter(new FileWriter(TASK_FILE))) {
             for (Task task : tasks){
                // System.out.println(task.getTaskID());
                 printWriter.println(
                         String.valueOf(task.getTaskID())+ ',' +
                         task.getProductType().toString() + ',' +
                         task.getQuantityRequired()+ ',' +
                         task.getProductLineID()+ ','+
                         task.getClientName()+ ',' +
                         task.getTaskStatus()+ ',' +
                         task.getStartDate()+ ',' +
                         task.getDeliveryDate()+ ','+
                         task.getProgress()
                 );

             }
         }catch (IOException e){
             System.out.println(e);// insert e in logError function
         }
    }
    public void loadTasks (){
        File file = new File(TASK_FILE);
        if (!file.exists()) return;
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String Line;
            while ((Line = br.readLine())!=null){
                String data[] = Line.split(",");
                Task t = new Task(
                        Integer.parseInt(data[2]),//QTY
                        productTypeRepository.findByFullName(data[1]), //ProductName
                        data[4], // clientName
                        Integer.parseInt(data[3]),// PL-ID
                        LocalDate.parse(data[7]) // D-date
                );
                t.setTaskStatus(TaskStatus.valueOf(data[5]));
                if (!"null".equals(data[6]))
                    t.setStartDate(LocalDate.parse(data[6]));
                double p = Double.parseDouble(data[8]);
                t.setProgress(p);
                t.setSaved(false);
                tasks.add(t);
            }
        } catch (Exception e){
            System.out.println(e);//to logError file
        }

    }

    public ArrayList<Task> getTasksByStatus(TaskStatus status) {
         ArrayList<Task>tasks1=new ArrayList<>() ;
         for (Task t:tasks)
         {
             if (t.getTaskStatus()==status)
                 tasks1.add(t);
         }
         return tasks1;
    }

    public ProductTypeRepository getProductTypeRepository() {
        return productTypeRepository;
    }
    public void update(Task updatedTask) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getTaskID() == updatedTask.getTaskID()) {
                tasks.set(i, updatedTask);
                return;
            }
        }
    }

}
