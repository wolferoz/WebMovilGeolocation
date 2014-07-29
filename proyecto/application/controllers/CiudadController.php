<?php

class CiudadController extends Zend_Controller_Action
{

    public function init()
    {
        $this->view->baseUrl=$this->getRequest()->getBaseUrl();
        $this->session = new Zend_Session_Namespace('usuario');
        $this->UserInfo= new Application_Model_DbTable_Usuario();
        $this->PostDetalle=new Application_Model_DbTable_Post();
        $this->Provincia=new Application_Model_DbTable_Provincias();

        if(isset($this->session->id_usuario)){
            $this->view->Info=$this->UserInfo->User_Dato($this->session->id_usuario);
        }
    }

    public function indexAction()
    {
           $provincia=$this->getRequest()->getParam("pro");
           $pais=$this->getRequest()->getParam("pais");
           $pais=1;
           $provincia=$this->Provincia->provincia_nombre($provincia);
           foreach ($provincia as $row){
               $id_provincia=$row['id_provincia'];
               $id_region=$row['id_regiones'];
           }
            $datos=$this->PostDetalle->Provincia($id_provincia);
            if($n==0){
                 $datos=$this->PostDetalle->Region($id_region);
                 $n=count($datos);
                 if($n==0){
                     $datos=$this->PostDetalle->Pais($pais);

                 }
            }
           $this->view->rand=$this->PostDetalle->Rand();
           $this->view->Datos=$datos;
    }


}

