package api;

// ApiService.java
import java.util.List;

import br.com.listaescola.Aluno;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("alunos") // Substitua "alunos" pelo endpoint correto da sua API
    Call<List<Aluno>> getAlunos();
}
