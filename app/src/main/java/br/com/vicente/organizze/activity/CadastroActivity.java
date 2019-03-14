package br.com.vicente.organizze.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import br.com.vicente.organizze.R;
import br.com.vicente.organizze.config.ConfiguracaoFirebase;
import br.com.vicente.organizze.helper.Base64Custom;
import br.com.vicente.organizze.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button botaoCadastrar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        getSupportActionBar().setTitle("");

        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        botaoCadastrar = findViewById(R.id.buttonCadastrar);

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 String textoNome = campoNome.getText().toString();
                 String textoEmail = campoEmail.getText().toString();
                 String textoSenha = campoSenha.getText().toString();
                 
                 if( !textoNome.isEmpty() ){
                     if( !textoEmail.isEmpty() ){
                        if( !textoSenha.isEmpty() ){

                            usuario = new Usuario();
                            usuario.setNome( textoNome );
                            usuario.setEmail( textoEmail );
                            usuario.setSenha( textoSenha );
                            cadastrarUsuario();

                        } else {
                            Toast.makeText(CadastroActivity.this, "Provide a password.", Toast.LENGTH_SHORT).show();
                        }
                     } else {
                         Toast.makeText(CadastroActivity.this, "Provide an e-mail", Toast.LENGTH_SHORT).show();
                     }
                 } else {
                     Toast.makeText(CadastroActivity.this, "Provide a name.", Toast.LENGTH_SHORT).show();
                 }
            }
        });
    }

    public void cadastrarUsuario(){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if ( task.isSuccessful() ) {

                    String idUsuario = Base64Custom.codificarBase64( usuario.getEmail() );
                    usuario.setIdUsuario(idUsuario);
                    usuario.salvar();
                    finish();

                } else {
                    String excecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        excecao = "Please provide a stronger password.";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        excecao = "Please provide a valid e-mail address.";
                    } catch (FirebaseAuthUserCollisionException e ){
                        excecao = "This e-mail is already in use.";
                    } catch (Exception e) {
                        excecao = "There was an error creating your account.";
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
