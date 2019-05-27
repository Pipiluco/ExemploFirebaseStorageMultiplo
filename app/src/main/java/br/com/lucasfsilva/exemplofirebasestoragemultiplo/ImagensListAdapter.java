package br.com.lucasfsilva.exemplofirebasestoragemultiplo;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImagensListAdapter extends RecyclerView.Adapter<ImagensListAdapter.ViewHolder> {
    public List<String> listNomesArquivos;
    public List<String> listImagensEnviadas;
    public List<Uri> listImagens;
    public Context context;

    public ImagensListAdapter(List<String> listNomesArquivos, List<String> listImagensEnviadas, List<Uri> listImagens, Context context) {
        this.listNomesArquivos = listNomesArquivos;
        this.listImagensEnviadas = listImagensEnviadas;
        this.listImagens = listImagens;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_imagem, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int posicao) {
        String nomeArquivo = listNomesArquivos.get(posicao);
        String imagemArquivo = listImagensEnviadas.get(posicao);
        Uri imagem = listImagens.get(posicao);

        viewHolder.tvImagem.setText(nomeArquivo);

        Picasso.with(context).load(imagem).into(viewHolder.imvImagem);

        if (imagemArquivo.equals("Carregando")) {
            viewHolder.imvProgresso.setImageResource(R.drawable.ic_loading_01);
        } else {
            viewHolder.imvProgresso.setImageResource(R.mipmap.checked);
        }
    }

    @Override
    public int getItemCount() {
        return listNomesArquivos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvImagem;
        public ImageView imvProgresso;
        public ImageView imvImagem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvImagem = (TextView) itemView.findViewById(R.id.tvImagem);
            imvProgresso = (ImageView) itemView.findViewById(R.id.imvProgresso);
            imvImagem = (ImageView) itemView.findViewById(R.id.imvImagem);

        }
    }
}
