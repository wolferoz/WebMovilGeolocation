<?php

class Application_Model_Post
{
  private $_table;

function __construct(){
    $this->_table=new Application_Model_DbTable_Post();

}

public function guardar($id_post,$titulo,$img,$descripcion,$id_usuario,$permiso,$categoria)
{
if ($id_post==0)
{
    $row=  $this->_table->createRow();
}else
{
    $row=  $this->_table->find($id_post)->current();
}
//$row->id_personal=$persona->getId();
//date_default_timezone_set('America/Lima');
$fechaActual= new Zend_Date();
$fechaActual->setTimezone('America/Lima');
$row->titulo=$titulo;
$row->imagen=$img;
$row->fecha=$fechaActual->get("Y-M-d H:m:s");
$row->descripcion=$descripcion;
$row->id_usuario=$id_usuario;
$row->permiso=$permiso;
$row->id_categoria=$categoria;
$row->save();
}


}

