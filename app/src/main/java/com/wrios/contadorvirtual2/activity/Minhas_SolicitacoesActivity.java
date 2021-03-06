package com.wrios.contadorvirtual2.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.wrios.contadorvirtual2.R;
import com.wrios.contadorvirtual2.adapter.AdapterSolicitacoesFiscal;
import com.wrios.contadorvirtual2.adapter.RecyclerItemClickListener;
import com.wrios.contadorvirtual2.helper.ConfiguracaoFirebase;
import com.wrios.contadorvirtual2.model_domain.Solicitacao;
import com.wrios.contadorvirtual2.model_domain.SolicitacaoSetorFiscal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class Minhas_SolicitacoesActivity extends AppCompatActivity {

    private RecyclerView recycleListSolicitacoes;
    private List<SolicitacaoSetorFiscal> solicitacoes = new ArrayList<>();
    private AdapterSolicitacoesFiscal adapterSolicitacoesFiscal;
    private DatabaseReference solicitacaoUsuariosRef;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhas__solicitacoes);

        //configuracoes iniciais

        solicitacaoUsuariosRef = ConfiguracaoFirebase.getReferenciaFirebaseDatabase()
                .child("minhas_solicitacoes")
                .child(ConfiguracaoFirebase.getIDUsuario());//recuperando o id do usuario para passar a solicitacao

        inicializarComponentes();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cadastrarSoli = new Intent(Minhas_SolicitacoesActivity.this, SolicitacaoAreaActivity.class);
                startActivity(cadastrarSoli);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //configurar RecyclerView
        recycleListSolicitacoes.setLayoutManager(new LinearLayoutManager(this));
        recycleListSolicitacoes.setHasFixedSize(true);
        //passando o adpater de solicitacao - Classe referete AdapterSolicitacoesFiscal
        adapterSolicitacoesFiscal = new AdapterSolicitacoesFiscal(solicitacoes,this);
        recycleListSolicitacoes.setAdapter(adapterSolicitacoesFiscal);

        //recuperar anuncios
        recuperaSolicitacaoes();

        //adionar  evento de clique no recyclerview para excluir a solicitacao
        recycleListSolicitacoes.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recycleListSolicitacoes,
                new RecyclerItemClickListener.OnItemClickListener() {

                    // DIFERENTES METODOS PARA EXCLUIR O ANUNCIO

                    @Override
                    public void onItemClick(View view, int position) {


                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                        SolicitacaoSetorFiscal solictacao_selecionada =  solicitacoes.get(position); //passando para a variavel a posicao da solicitacao a ser removida
                        solictacao_selecionada.remover();
                        adapterSolicitacoesFiscal.notifyDataSetChanged();//para dizer que os dados foram alterados


                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

    }
    //recuperar os solicitacao
    private void recuperaSolicitacaoes(){
        //abro o dialog para carregando as solicitacoes
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando solicitações")
                .setCancelable(false)
                .build();
        alertDialog.show();

        solicitacaoUsuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            //recuoera os dados
            public void onDataChange(DataSnapshot dataSnapshot) {

                solicitacoes.clear();//limpar solicitacoes para buscar
                for (DataSnapshot ds : dataSnapshot.getChildren() ){ // passar os anuncios para a lista
                    solicitacoes.add(ds.getValue(SolicitacaoSetorFiscal.class));
                }
                Collections.reverse(solicitacoes); // para fazer uma exibiçao reversa dos anuncios
                adapterSolicitacoesFiscal.notifyDataSetChanged(); //

                alertDialog.dismiss();//fechar o dialog pois as solicitacoes foram carregadas

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void inicializarComponentes(){
        recycleListSolicitacoes = findViewById(R.id.recyclerSolicitacoesCliente);
    }

}
