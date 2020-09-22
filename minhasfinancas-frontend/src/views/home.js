
import React from "react";
import currencyFormatter from "currency-formatter";

import UsuarioService from "../app/services/usuarioService";
import { ContextoAutenticacao } from "../main/provedorAutenticacao";


class Home extends React.Component {
  state = {
    saldo: 0,
  };

  constructor() {
    super()
    this.usuarioService = new UsuarioService();
  }

  componentDidMount() {
    const usuarioLogado = this.context.usuarioAutenticado;

    this.usuarioService.obterSaldoPorUsuario(usuarioLogado.id)
      .then(response => {
        this.setState({ saldo: response.data })
      }).catch(error => {
        console.error(error.response)
      });
  }

  render() {
    return (
      <div className="jumbotron">
        <h1 className="display-3">Bem vindo!</h1>
        <p className="lead">Esse é seu sistema de finanças.</p>
        <p className="lead">
          Seu saldo para o mês atual é de {currencyFormatter.format(this.state.saldo, { locale: "pt-br" })}
        </p>
        <hr className="my-4" />
        <p>
          E essa é sua área administrativa, utilize um dos botões abaixo para navegar pelo sistema.
        </p>
        <p className="lead">
          <a className="btn btn-primary btn-lg"
            href="#/cadastro-usuario"
            role="button"><i className="fa fa-users"></i> <i className="pi pi-users"></i> Cadastrar Usuário</a>
          <a className="btn btn-danger btn-lg"
             href="#/cadastro-lancamentos"
            role="button"><i className="fa fa-users"></i> <i className="pi pi-money-bill"></i> Cadastrar Lançamento</a>
        </p>
      </div>
    );
  }
}

Home.contextType = ContextoAutenticacao;

export default Home;
