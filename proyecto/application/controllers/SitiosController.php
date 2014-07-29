<?php

class SitiosController extends Zend_Controller_Action
{

    public function init()
    {
        //tablas
        $this->view->baseUrl=$this->getRequest()->getBaseUrl();
        $this->session = new Zend_Session_Namespace('usuario');
        $this->UserInfo= new Application_Model_DbTable_Usuario();
        $this->Post=new Application_Model_Post();

        //modelos
        //exite session..??
        if(isset($this->session->id_usuario)){
            $this->view->Info=$this->UserInfo->User_Dato($this->session->id_usuario);
        }
    }

    public function indexAction()
    {
                $this->Regiones=new Application_Model_DbTable_Regiones();
        var_dump($this->Regiones->All(1));exit;
    }

    public function paisAction()
    {
        // action body
    }

    public function regionAction()
    {
         $this->_helper->layout()->disableLayout();
        $Id=$this->getRequest()->getParam("pais");
        
        $this->Regiones=new Application_Model_DbTable_Regiones();
        $this->view->Region=$this->Regiones->All($Id);
    }

    public function provinciaAction()
    {
         $this->_helper->layout()->disableLayout();
        $Id=$this->getRequest()->getParam("region");

        $this->Provincia=new Application_Model_DbTable_Provincias();
        $this->view->provincia=$this->Provincia->All($Id);
    }

    public function distritoAction()
    {
         $this->_helper->layout()->disableLayout();
        $Id=$this->getRequest()->getParam("provincia");

        $this->Distrito=new Application_Model_DbTable_Distrito();
        $this->view->distrito=$this->Distrito->All($Id);
    }


}









