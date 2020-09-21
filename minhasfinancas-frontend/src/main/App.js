import React from "react";

import Rotas from "./rotas";
import Navbar from "../components/navbar";
import ProvedorAutenticacao from "./provedorAutenticacao";

import "toastr/build/toastr.min.js"

import "bootswatch/dist/flatly/bootstrap.css";
import "../custom.css";
import "toastr/build/toastr.css"

import 'primereact/resources/themes/nova/theme.css';
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';

class App extends React.Component {
  render() {
    return (
      <ProvedorAutenticacao>
        <Navbar />
        <div className="container">  
          <Rotas />
        </div>
      </ProvedorAutenticacao>
    );
  }
}

export default App;