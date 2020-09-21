import React from "react";
import Card from "../components/card";
import FormGroup from "../components/form-group";
import { withRouter } from "react-router-dom";

import UsuarioService from "../app/services/usuarioService";
import * as messages from "../components/toastr";

class CadastroUsuario extends React.Component {
  state = {
    nome: "",
    email: "",
    senha: "",
    senhaRepeticao: "",
  };

  constructor() {
    super();
    this.service = new UsuarioService();
  }

  cadastrar = () => {

    const { nome, email, senha, senhaRepeticao } = this.state

    const usuario = { nome, email, senha, senhaRepeticao };

    try {
      this.service.validar(usuario)
    } catch (erro) {
      const mensagens = erro.mensagens;
      mensagens.forEach(msg => messages.mensagemErro(msg));
      return false;
    }

    this.service
      .salvar(usuario)
      .then((response) => {
        messages.mensagemSucesso(
          "Usuario Cadastrado com sucesso! Faça o login para acessar o sistema."
        );
        this.props.history.push("/login");
      })
      .catch((error) => {
        messages.mensagemErro(error.response.data);
      });
  };

  cancelar = () => {
    this.props.history.push("/login");
  };

  render() {
    return (
      <Card title="Cadastro de Usuário">
        <div className="row">
          <div className="col-lg-12">
            <div className="bs-component">
              <FormGroup label="Nome: *" htmlFor="inputNome">
                <input
                  type="text"
                  id="inputNome"
                  className="form-control"
                  name="nome"
                  placeholder="Digite seu nome"
                  onChange={(e) => this.setState({ nome: e.target.value })}
                />
              </FormGroup>

              <FormGroup label="Email: *" htmlFor="inputEmail">
                <input
                  type="email"
                  id="inputEmail"
                  className="form-control"
                  name="email"
                  placeholder="Digite um email válido"
                  onChange={(e) => this.setState({ email: e.target.value })}
                />
              </FormGroup>

              <FormGroup label="Senha: *" htmlFor="inputSenha">
                <input
                  type="password"
                  id="inputSenha"
                  className="form-control"
                  name="senha"
                  placeholder="Digite uma senha com no máximo 20 caracteres"
                  onChange={(e) => this.setState({ senha: e.target.value })}
                />
              </FormGroup>

              <FormGroup label="Repita a senha: *" htmlFor="inputRepitaSenha">
                <input
                  type="password"
                  id="inputRepitaSenha"
                  className="form-control"
                  name="repitaSenha"
                  placeholder="Repita a senha informada anteriomente"
                  onChange={(e) =>
                    this.setState({ senhaRepeticao: e.target.value })
                  }
                />
              </FormGroup>
              <button
                onClick={this.cadastrar}
                type="button"
                className="btn btn-success"
              ><i className="pi pi-save"></i> Salvar
              </button>
              <button
                onClick={this.cancelar}
                type="button"
                className="btn btn-danger"
              > <i className="pi pi-undo"></i> Voltar
              </button>
            </div>
          </div>
        </div>
      </Card>
    );
  }
}

export default withRouter(CadastroUsuario);
