package br.com.listaescola;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

import api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ListView studentsListView;
    private ArrayAdapter<Aluno> adapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        studentsListView = findViewById(R.id.studentsListView);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.alu.com/alunos")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        carregarAlunos();
    }

    // Método para encontrar o tamanho da Maior Subsequência Comum entre duas strings
    private int findLCSLength(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++) {
            for (int j = 0; j <= str2.length(); j++) {
                if (i == 0 || j == 0)
                    dp[i][j] = 0;
                else if (str1.charAt(i - 1) == str2.charAt(j - 1))
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                else
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }

        return dp[str1.length()][str2.length()];
    }

    // Método para encontrar a Maior Subsequência Comum entre dois alunos
    private String findLCSBetweenAlunos(Aluno aluno1, Aluno aluno2) {
        String nomeDescricao1 = aluno1.getNomeDescricao();
        String nomeDescricao2 = aluno2.getNomeDescricao();
        int lcsLength = findLCSLength(nomeDescricao1, nomeDescricao2);

        // Implementação para obter a própria subsequência comum (opcional)
        char[] lcsArray = new char[lcsLength];
        int[][] dp = new int[nomeDescricao1.length() + 1][nomeDescricao2.length() + 1];
        int i = nomeDescricao1.length(), j = nomeDescricao2.length();
        while (i > 0 && j > 0) {
            if (nomeDescricao1.charAt(i - 1) == nomeDescricao2.charAt(j - 1)) {
                lcsArray[--lcsLength] = nomeDescricao1.charAt(i - 1);
                i--;
                j--;
            } else if (dp[i - 1][j] > dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }
        return new String(lcsArray);
    }

    // Método para carregar os alunos da API
    private void carregarAlunos() {
        Call<List<Aluno>> call = apiService.getAlunos();
        call.enqueue(new Callback<List<Aluno>>() {
            @Override
            public void onResponse(Call<List<Aluno>> call, Response<List<Aluno>> response) {
                if (response.isSuccessful()) {
                    List<Aluno> alunos = response.body();
                    if (alunos != null && !alunos.isEmpty()) {
                        adapter = new ArrayAdapter<>(MainActivity.this,
                                android.R.layout.simple_list_item_1, alunos);
                        studentsListView.setAdapter(adapter);

                        // Exemplo de como encontrar a maior subsequência comum entre os dois primeiros alunos
                        if (alunos.size() >= 2) {
                            Aluno aluno1 = alunos.get(0);
                            Aluno aluno2 = alunos.get(1);

                            String lcs = findLCSBetweenAlunos(aluno1, aluno2);
                            Log.d("LCS", "Maior subsequência comum: " + lcs);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Lista de alunos vazia",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Falha ao obter alunos",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Aluno>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erro: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
