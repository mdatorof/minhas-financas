import React from "react";
import Login from "../views/login";
import Home from "../views/home";
import CadastroUsuario from "../views/cadastroUsuario";
import ConsultaLancamentos from "../views/lancamentos/consultaLancamentos";
import CadastroLancamentos from "../views/lancamentos/cadastroLancamentos";
import { ConsumidorAutenticacao } from "../main/provedorAutenticacao";

import { Route, Switch, HashRouter, Redirect} from "react-router-dom";

function RotaAutenticada({component: Component, seUsuarioAutenticado, ...props}) {
    return (
        <Route {...props} render={(componentProps) => {
            if(seUsuarioAutenticado) {
                return (
                    <Component {...componentProps}/>
                )
            } else {
                return (<Redirect to={{pathname: '/login', state: {from: componentProps.location }}} />)
            }
        }} />
    )
}

function Rotas(props) {
    return (
        <HashRouter>
            <Switch>
                <Route path="/login" component={Login} />  
                <Route path="/cadastro-usuario" component={CadastroUsuario} />
        
                <RotaAutenticada seUsuarioAutenticado= {props.seUsuarioAutenticado} path="/home" component ={Home} />
                <RotaAutenticada seUsuarioAutenticado= {props.seUsuarioAutenticado} path="/consulta-lancamentos" component={ConsultaLancamentos} />
                <RotaAutenticada seUsuarioAutenticado= {props.seUsuarioAutenticado} path="/cadastro-lancamentos/:id?" component={CadastroLancamentos} />
            </Switch>
        </HashRouter>
    )
}

export default () => (
    <ConsumidorAutenticacao>
        {(context) => (<Rotas seUsuarioAutenticado={context.seAutenticado} />)}
    </ConsumidorAutenticacao>
)

