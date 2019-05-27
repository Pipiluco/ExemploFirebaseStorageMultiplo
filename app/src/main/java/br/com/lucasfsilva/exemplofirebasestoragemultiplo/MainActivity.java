package br.com.lucasfsilva.exemplofirebasestoragemultiplo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 1;
    private ImageButton imbEscolhaImagem;
    private RecyclerView rcyImagens;

    private List<String> listNomesArquivos;
    private List<String> listImagensEnviadas;
    private List<Uri> listImagens;
    private int totalImagensList = 0;

    private ImagensListAdapter imagensListAdapter;

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storageReference = FirebaseStorage.getInstance().getReference();

        imbEscolhaImagem = (ImageButton) findViewById(R.id.imbEscolhaImagem);
        rcyImagens = (RecyclerView) findViewById(R.id.rcyImagens);

        listNomesArquivos = new ArrayList<>();
        listImagensEnviadas = new ArrayList<>();
        listImagens = new ArrayList<>();
        imagensListAdapter = new ImagensListAdapter(listNomesArquivos, listImagensEnviadas, listImagens, getApplicationContext());

        rcyImagens.setLayoutManager(new LinearLayoutManager(this));
        rcyImagens.setHasFixedSize(true);
        rcyImagens.setAdapter(imagensListAdapter);

        imbEscolhaImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Escolha imagem"), RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                // Toast.makeText(getApplicationContext(), "Multiplas seleção de imagens!", Toast.LENGTH_SHORT).show();
                int totalImagensSelecionadas = data.getClipData().getItemCount();

                for (int i = 0; i < totalImagensSelecionadas; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    String nomeArquivo = getNomeArquivo(uri);

                    listNomesArquivos.add(nomeArquivo);
                    listImagensEnviadas.add("Carregando");
                    listImagens.add(uri);
                    imagensListAdapter.notifyDataSetChanged();

                    StorageReference arquivoParaUpload = storageReference.child("Imagens").child(nomeArquivo);

                    final int finalI = i;
                    arquivoParaUpload.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            listImagensEnviadas.remove(finalI);
                            listImagensEnviadas.add(finalI, "Concluído");
                            imagensListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            } else if (data.getData() != null) {
                // Toast.makeText(getApplicationContext(), "Simple seleção de imagem!", Toast.LENGTH_SHORT).show();
                Uri uri = data.getData();
                String nomeArquivo = getNomeArquivo(uri);

                listNomesArquivos.add(nomeArquivo);
                listImagensEnviadas.add("Carregando");
                listImagens.add(uri);
                imagensListAdapter.notifyDataSetChanged();

                StorageReference arquivoParaUpload = storageReference.child("Imagens").child(nomeArquivo);

                arquivoParaUpload.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        listImagensEnviadas.remove(totalImagensList);
                        listImagensEnviadas.add(totalImagensList, "Concluído");
                        imagensListAdapter.notifyDataSetChanged();
                        totalImagensList = totalImagensList + 1;
                    }
                });
            }
        }
    }

    public String getNomeArquivo(Uri uri) {
        String resultado = null;

        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    resultado = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }

        if (resultado == null) {
            resultado = uri.getPath();
            int corte = resultado.lastIndexOf('/');
            if (corte != -1) {
                resultado = resultado.substring(corte + 1);
            }
        }

        return resultado;
    }
}
