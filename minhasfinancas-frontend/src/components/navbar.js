import React from "react";

import NavbarItem from "./navbaritem";
import { ConsumidorAutenticacao } from "../main/provedorAutenticacao";

function Navbar(props) {
    return (
        <div
            className="navbar navbar-expand-lg fixed-top navbar-dark bg-primary">
            <div className="container">
                <a href="#/login" className="navbar-brand">Minhas Finanças</a>
                <button
                    className="navbar-toggler"
                    type="button"
                    data-toggle="collapse"
                    data-target="#navbarResponsive"
                    aria-controls="navbarResponsive"
                    aria-expanded="false"
                    aria-label="Toggle navigation"
                >
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="navbarResponsive">
                    <ul className="navbar-nav">
                        <NavbarItem render={props.seUsuarioAutenticado} href="#/home" label="Home" />
                        <NavbarItem render={props.seUsuarioAutenticado} href="#/cadastro-usuario" label="Usuários" />
                        <NavbarItem render={props.seUsuarioAutenticado} href="#/consulta-lancamentos" label="Lançamentos" />
                        <NavbarItem render={props.seUsuarioAutenticado} onClick={props.deslogar} href="#/login" label="Sair" />
                    </ul>
                </div>
            </div>
        </div>
    );
}

export default () => (
    <ConsumidorAutenticacao>
        {(context) => (<Navbar seUsuarioAutenticado={context.seAutenticado}
            deslogar={context.encerrarSessao} />)}
    </ConsumidorAutenticacao>
)