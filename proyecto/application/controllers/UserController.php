<?php

class UserController extends Zend_Controller_Action
{

    public function init()
    {
        //tablas
        $this->view->baseUrl=$this->getRequest()->getBaseUrl();
        $this->session = new Zend_Session_Namespace('usuario');
        $this->UserInfo= new Application_Model_DbTable_Usuario();
        $this->Registro= new Application_Model_Usuario();
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
        $this->view->robotos="noindex";
    }

    public function indexAction()
    {
       $this->_redirect("");
    }

    public function logueoAction()
    {

            $Correo=$this->getRequest()->getParam("txtAlias");
            $Contra=$this->getRequest()->getParam("txtPassword");
            if(isset($Correo) and isset($Contra)){
            $authAdapter = new Zend_Auth_Adapter_DbTable();
            $authAdapter
                ->setTableName('usuario')
                ->setIdentityColumn('alias')
                ->setCredentialColumn('password');

            $authAdapter
                ->setIdentity($Correo)
                ->setCredential($Contra);

            $auth = Zend_Auth::getInstance();

            $result = $auth->authenticate($authAdapter);

            if(  $result->isValid() ){
                /*Zend_Registry::set('authAdapter', );
                $value = Zend_Registry::get('authAdapter');
                var_dump($value);*/
                $datos=$authAdapter->getResultRowObject();
                $session = new Zend_Session_Namespace('usuario');
                $session->id_usuario=$datos->id_usuario;
                $session->alias=$datos->alias;
                $session->pais=$datos->pais;
                $session->region=$datos->region;
                $session->provincia=$datos->provincia;
                $session->distrito=$datos->distrito;
                $this->_redirect('');
            }else{
                echo "verifique sus datos";
            }
            }else{
                $this->_redirect('');
            }
    }

    public function registroAction()
    {
        $Pseudonimo=strtolower($this->getRequest()->getParam("txtPseudonimo"));
        $Correo=$this->getRequest()->getParam("txtCorreo");
        $Contrasena=$this->getRequest()->getParam("txtContrasena");
        $Contrasena2=$this->getRequest()->getParam("txtContrasena2");

        if($Pseudonimo!=" " && $Correo!=" " && $Contrasena!=" " && $Contrasena2!=" "){
            if($Contrasena==$Contrasena2){
            $fechaActual= new Zend_Date();
            $fechaActual->setTimezone('America/Lima');
            $this->Registro->guardar(0, $Correo, $Pseudonimo, $Contrasena, 'img/perfil.jpg',$fechaActual->get("Y-M-d H:m:s"));
            //buscamos al ultimo usuario agregado
            $datos=$this->UserInfo->Existe_Email($Correo);
            foreach($datos as $row){
                $session = new Zend_Session_Namespace('usuario');
                $session->id_usuario=$row['id_usuario'];
                $session->correo=$row['correo'];
                $session->pseudonimo=$row['pseudonimo'];
                $this->_redirect('');
            }
            }else{
                $this->_redirect('');
            }
        }else{
            $this->_redirect('');
        }
    }

    public function logoutAction()
    {
    unset($this->session->id_usuario);
    unset($this->session->correo);
    unset($this->session->pseudonimo);
    $this->_redirect();
    }

    public function configAction()
    {
       if(isset($this->session->id_usuario)){
           $Modo=$this->getRequest()->getParam("modo");
           $datos=$this->UserInfo->User_Dato($this->session->id_usuario);
           $this->view->datos=$datos;
           foreach($datos as $row){
               $correo=$row['correo'];
               $pseudonimo=$row['pseudonimo'];
               $password=$row['password'];
               $imagen=$row['imagen'];
               $fecha=$row['fecha'];
           }
           if($Modo=="a"){
              $Alias=$this->getRequest()->getParam("txtAlias");
              $this->Registro->guardar($this->session->id_usuario, $correo, $Alias, $password, $imagen,$fecha);
              $this->_redirect("user/config");
           }
           if($Modo=="b"){
               $Corre=$this->getRequest()->getParam("txtCorreo");
              $this->Registro->guardar($this->session->id_usuario, $Corre, $pseudonimo, $password, $imagen,$fecha);
              $this->_redirect("user/config");
           }
           if($Modo=="c"){
              $Contra=$this->getRequest()->getParam("txtcontraante");
              if($Contra==$row['password']){
              $ContraNew=$this->getRequest()->getParam("txtcontranew");
              $this->Registro->guardar($this->session->id_usuario, $correo, $pseudonimo, $ContraNew, $imagen,$fecha);
              $this->_redirect("user/config");   
              }else{
                  $this->_redirect("user/config/modo/alerta");
              }
           }
           if($Modo=="alerta"){
            $this->view->alerta="error contraseña incorrecta";
           }
       }else{
         $this->_redirect();
       }
    }

    public function subirAction()
    {
     $this->_helper->layout()->disableLayout();
    $caracteres=10;
    $random_pass = substr(md5(rand()),0,$caracteres);

function redimensionar_jpeg($img_original, $img_nueva, $img_nueva_anchura, $img_nueva_altura, $img_nueva_calidad)
{
	// crear una imagen desde el original
	$img = ImageCreateFromJPEG($img_original);
	// crear una imagen nueva
	$thumb = imagecreatetruecolor($img_nueva_anchura,$img_nueva_altura);
	// redimensiona la imagen original copiandola en la imagen
	ImageCopyResized($thumb,$img,0,0,0,0,$img_nueva_anchura,$img_nueva_altura,ImageSX($img),ImageSY($img));
 	// guardar la nueva imagen redimensionada donde indicia $img_nueva
	ImageJPEG($thumb,$img_nueva,$img_nueva_calidad);
	ImageDestroy($img);
}

$numero =  $random_pass;
$uploaddir = 'uploads/';
$uploadfile = $uploaddir . basename($_FILES['userfile']['name']);
if (move_uploaded_file($_FILES['userfile']['tmp_name'], $uploadfile)) {
	  $extension_file = explode(".",$uploadfile);
	  $num = count($extension_file)-1;
	  if($extension_file[$num] == "gif"){
	  $imagenxd = imagecreatefromgif($uploadfile);
	  $patch_grabar='uploads/'.$numero.'.jpg';
	  }
	  if($extension_file[$num] == "png"){
	  $imagenxd = imagecreatefrompng($uploadfile);
	  $patch_grabar='uploads/'.$numero.'.jpg';
	  }
	  if($extension_file[$num] == "jpg" || $extension_file[$num] == "jpeg"){
	  $imagenxd = imagecreatefromjpeg($uploadfile);
	  $patch_grabar='uploads/'.$numero.'.jpg';
	  }
	  imagejpeg($imagenxd,$patch_grabar,100);
	  $imagen ='uploads/'.$numero.'.jpg';
	  $nombre_imagen_asociada =$numero.'.jpg'; 
          redimensionar_jpeg($imagen, $imagen, 125, 125, 100);
           echo $nombre_imagen_asociada;
	  unlink($uploadfile);
           $datos=$this->UserInfo->User_Dato($this->session->id_usuario);
           foreach($datos as $row){
               $correo=$row['correo'];
               $pseudonimo=$row['pseudonimo'];
               $password=$row['password'];
               $fecha=$row['fecha'];
           }
           $this->Registro->guardar($this->session->id_usuario, $correo, $pseudonimo, $password, $imagen,$fecha);

}
    }


}













