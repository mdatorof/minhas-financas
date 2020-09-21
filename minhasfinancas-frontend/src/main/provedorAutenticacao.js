import React from "react";
import AutenticarService from "../app/services/autenticarService";

export const ContextoAutenticacao = React.createContext();
export const ConsumidorAutenticacao = ContextoAutenticacao.Consumer;

const ProverAutenticacao = ContextoAutenticacao.Provider;

class ProvedorAutenticacao extends React.Component{

    state = {
        usuarioAutenticado: null,
        seAutenticado: false
    }

    iniciarSessao = (usuario) => {
        AutenticarService.logar(usuario);
        this.setState({seAutenticado: true, usuarioAutenticado: usuario});
    }

    encerrarSessao = () => {
        AutenticarService.removerUsuarioAutenticado();
        this.setState({seAutenticado: false, usuarioAutenticado: null });
    }

    render() {

        const contexto = {
            usuarioAutenticado: this.state.usuarioAutenticado,
            seAutenticado: this.state.seAutenticado,
            iniciarSessao: this.iniciarSessao,
            encerrarSessao: this.encerrarSessao
        }

        return(
            <ProverAutenticacao value ={contexto}>
                {this.props.children}
            </ProverAutenticacao>
        )
    }
}

export default ProvedorAutenticacao;