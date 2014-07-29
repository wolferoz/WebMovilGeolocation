<?php

class PostController extends Zend_Controller_Action
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
        //etiquetas

        //tags
    }

    public function indexAction()
    {

    }

    public function guardarAction()
    {
if(isset($this->session->id_usuario)){
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
    $caracteres=10;
    $random_pass = substr(md5(rand()),0,$caracteres);
    $numero = $random_pass ;
    $uploaddir = 'uploads/';
    $uploadfile = $uploaddir . basename($_FILES['File']['name']);
    if (move_uploaded_file($_FILES['File']['tmp_name'], $uploadfile)) {
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
              redimensionar_jpeg($imagen, $imagen, 750, 390, 100);
              unlink($uploadfile);
    }
            $Titulo=$this->getRequest()->getParam("Titulo");
            $Categoria=$this->getRequest()->getParam("Categoria");
            $Descripcion=$this->getRequest()->getParam("Descripcion");
            $pais=$this->getRequest()->getParam("pais");
            $region=$this->getRequest()->getParam("region");
            $provincia=$this->getRequest()->getParam("provincia");
            $distrito=$this->getRequest()->getParam("distrito");
           
               if($Titulo!="" && $Categoria!="" && $Descripcion!=""){
                   $this->Post->guardar(0, $Titulo, $imagen, $Descripcion, $this->session->id_usuario,0,1,$pais,$region,$provincia,$distrito);
                    $this->_redirect('');
               }
                $this->_redirect('');
          }
    }


}



