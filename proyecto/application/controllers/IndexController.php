<?php

class IndexController extends Zend_Controller_Action
{

    public function init()
    {
        $this->view->baseUrl=$this->getRequest()->getBaseUrl();
        $this->session = new Zend_Session_Namespace('usuario');
        $this->UserInfo= new Application_Model_DbTable_Usuario();
        $this->PostDetalle=new Application_Model_DbTable_Post();
    }

    public function indexAction()
    {

        // si el usuario esta logueado realizamos los siguiente
        if(isset($this->session->id_usuario)){
           
            $this->view->Info=$this->UserInfo->User_Dato($this->session->id_usuario);

        $datos=$this->PostDetalle->Distrito($this->session->distrito);
        $n=count($datos);
        if($n==0){  
            $datos=$this->PostDetalle->Provincia($this->session->provincia);
            $n=count($datos);   
            if($n==0){
                 $datos=$this->PostDetalle->Region($this->session->region);
                 $n=count($datos);
                 if($n==0){   
                     $datos=$this->PostDetalle->Pais($this->session->pais);
                     $n=count($datos);
                 }  
            } 
        }
        }else{
             $datos=$this->PostDetalle->ultimos10();
        }
        $this->view->rand=$this->PostDetalle->Rand();
        $this->view->Datos=$datos;
    }


}

