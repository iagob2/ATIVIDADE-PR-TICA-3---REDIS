package redis.ex_redis;

import redis.clients.jedis.Jedis;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ExRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExRedisApplication.class, args);

        try (Jedis jedis = new Jedis("redis://default:an2b1ew5rvkO2rAg0cuoJSurgZUYjYgx@redis-15894.c322.us-east-1-2.ec2.cloud.redislabs.com:15894")) {
            String Id1 = "1";
            String Descri1 = "Implementar autenticação de usuário";
            String Id2 = "2";
            String Descri2 = "Criar interface de usuário";
            String Id3 = "3";
            String Descri3 = "Criar codigo";

            // Armazenar as tarefas no Redis com IDs únicos
            jedis.set("task:" + Id1, Descri1);
            jedis.set("task:" + Id2, Descri2);
            jedis.set("task:" + Id3, Descri3);

            // Listar todas as tarefas
            System.out.print("listar de todas as tarefas: \n");
            list(jedis);

            // Simular marcação de uma tarefa como concluída
            String taskIdToComplete = "2";
            // Marcar a tarefa como concluída
            jedis.set("task:" + taskIdToComplete + ":status", "concluída");

            // Remover todas as tarefas concluídas
            List<String> complete = findComplete(jedis);
            
            for (String taskId : complete) {
                // Remover a chave da tarefa concluída
                System.out.print("listar de todas as tarefas concluidas:\n Tarefa: " + taskId);

                jedis.del("task:" + taskId, "task:" + taskId + ":status");
            }

            // Listar novamente as tarefas após a remoção
            System.out.println("\nLista de tarefas após a remoção das concluídas:");
            list(jedis);
        }
    }

    private static void list(Jedis jedis) {
        Set<String> keys = jedis.keys("task:*");
        for (String key : keys) {
            String taskId = key.split(":")[1]; // Extrair o ID da chave
            String taskDescription = jedis.get(key);
            System.out.println("Tarefa " + taskId + ": " + taskDescription);
        }
    }

    private static List<String> findComplete(Jedis jedis) {
        List<String> completedTasks = new ArrayList<>();
        Set<String> keys = jedis.keys("task:*:status");
        for (String key : keys) {
            String taskId = key.split(":")[1]; // Extrair o ID da chave
            String taskStatus = jedis.get(key);
            if (taskStatus.equals("concluída")) {
                completedTasks.add(taskId);
            }
        }
        
         return completedTasks;
    }
}
