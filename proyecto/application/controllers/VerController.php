<?php

class VerController extends Zend_Controller_Action
{

    public function init()
    {
        //tablas
        $this->view->baseUrl=$this->getRequest()->getBaseUrl();
        $this->session = new Zend_Session_Namespace('usuario');
        $this->UserInfo= new Application_Model_DbTable_Usuario();
        $this->Post=new Application_Model_Post();
        $this->PostDetalle=new Application_Model_DbTable_Post();
        $this->Voto=new Application_Model_DbTable_Voto();
        $this->VotoDetalle=new Application_Model_Voto();
        //modelos

        //exite session..??
        if(isset($this->session->id_usuario)){
            $this->view->Info=$this->UserInfo->User_Dato($this->session->id_usuario);
        }
        //etiquetas

        //tags
        $this->view->title="";
        $this->view->image="";
        $this->view->description="";
        $this->view->url="";
    }

    public function indexAction()
    {
       $ID=$this->getRequest()->getParam("n");
       $temp=end(explode("-",$ID));
       $ID=str_replace('.html','',$temp);
       
       if(isset($ID)){

           //contamos para ver si existe
           $n=count($this->PostDetalle->Id($ID));
           //si existe entramos

          if($n==1){
            $datos=$this->PostDetalle->Id($ID);
            $this->view->Voto=$this->Voto->All($ID);

            $this->view->Datos=$datos;
//            foreach($datos as $row){
//                $url=str_replace(' ','-',$row['titulo']);
//                $this->view->title="Tus Confesiones - ".$row['titulo'];
//                $this->view->image=$row['urlimg'];
//                $this->view->description=$row['descripcion'];
//                $this->view->url="http://tusconfesiones.com/ver/".$url.'-'.$row['id_confecion'].'.html';
//            }
          }else{
            $this->_redirect(); // nos redirigimos
          }
       }else{
           $this->_redirect();
       }
    }
    public function votoAction()
    {
     $favor=$this->getRequest()->getParam("favor");
     $contra=$this->getRequest()->getParam("contra");
     $id=$this->getRequest()->getParam("id");
     $idvo=$this->getRequest()->getParam("idvo");
     $this->VotoDetalle->guardar($idvo, $favor, $contra,$id);
    }


}

