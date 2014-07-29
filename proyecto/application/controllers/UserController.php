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
        $this->Post=new Application_Model_Post();
        $this->PostDetalle=new Application_Model_DbTable_Post();
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
                if( $datos->rol=='admin'){
                    $this->_redirect('/user/admin');
                }else{
                    $this->_redirect('');
                }
            }else{
                echo "verifique sus datos";
            }
            }else{
                $this->_redirect('');
            }
    }

    public function registroAction()
    {

        $submit=$this->getRequest()->getParam("submit");
       if(isset($submit)){
        $Nombre=strtolower($this->getRequest()->getParam("Nombre"));
        $Apellido=$this->getRequest()->getParam("Apellido");
        $Alias=$this->getRequest()->getParam("Alias");
        $Password=$this->getRequest()->getParam("Password");

        $pais=$this->getRequest()->getParam("pais");
        $region=$this->getRequest()->getParam("region");
        $provincia=$this->getRequest()->getParam("provincia");
        $distrito=$this->getRequest()->getParam("distrito");


        if($Nombre!=" " && $Apellido!=" " && $Alias!=" " && $Password!=" "){


            $this->Registro->guardar(0, $Nombre, $Apellido, $Password,"user",$Alias,$pais,$region,$provincia,$distrito);
            //buscamos al ultimo usuario agregado
            $datos=$this->UserInfo->Existe_alias($Alias);
            foreach($datos as $row){
                $session = new Zend_Session_Namespace('usuario');
                $session->id_usuario=$row['id_usuario'];
                $session->alias=$row['alias'];
                $session->pais=$row['pais'];
                $session->region=$row['region'];
                $session->provincia=$row['provincia'];
                $session->distrito=$row['distrito'];
                $this->_redirect('');
          }
        }else{
            $this->_redirect('');
        }
       }
    }

    public function logoutAction()
    {
    unset($this->session->id_usuario);
    unset($this->session->correo);
    unset($this->session->pseudonimo);
    $this->_redirect();
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
    public function adminAction()
    {
        if(isset($this->session->id_usuario)){
         $this->view->Usuarios=$this->UserInfo->Ultimos();
         $this->view->Post=$this->PostDetalle->ultimos();
        }else{
            $this->_redirect();
        }
    }
    public function rolAction()
    {
        if(isset($this->session->id_usuario)){
        $i=$this->getRequest()->getParam("i");
        $rol=$this->getRequest()->getParam("group$i");
        $Id=$this->getRequest()->getParam("id");
        $Usuario=$this->UserInfo->User_Id($Id);
        foreach($Usuario as $row){
            $nombre=$row['nombre'];
            $apellido=$row['apellido'];
            $password=$row['password'];
            $fecha=$row['fecha'];
            $alias=$row['alias'];
            $pais=$row['pais'];
            $region=$row['region'];
            $provincia=$row['provincia'];
            $distrito=$row['distrito'];
        }
        $this->Registro->guardar($Id, $nombre, $apellido, $password, $rol, $alias, $pais, $region, $provincia, $distrito);
        $this->_redirect("user/admin");
        }else{
         $this->_redirect();
        }
    }
    public function permisoAction()
    {
        if(isset($this->session->id_usuario)){
        $i=$this->getRequest()->getParam("i");
        $permiso=$this->getRequest()->getParam("group$i");
        $Id=$this->getRequest()->getParam("id");
        $Post=$this->PostDetalle->Id($Id);
        foreach($Post as $row){
            $titulo=$row['titulo'];
            $img=$row['imagen'];
            $descripcion=$row['descripcion'];
            $id_usuario=$row['id_usuario'];
            $categoria=$row['id_categoria'];
            $pais=$row['pais'];
            $region=$row['region'];
            $provincia=$row['provincia'];
            $distrito=$row['distrito'];
        }
        $this->Post->guardar($Id, $titulo, $img, $descripcion, $id_usuario, $permiso, $categoria, $pais, $region, $provincia, $distrito);
        $this->_redirect("user/admin");
        }else{
         $this->_redirect();
        }
    }
}













