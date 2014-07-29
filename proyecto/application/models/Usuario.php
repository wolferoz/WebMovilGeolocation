<?php

class Application_Model_Usuario
{

 private $_table;
 function __construct(){
    $this->_table=new Application_Model_DbTable_Usuario();

}
public function guardar($id_usuario,$nombre,$apellido,$password,$rol,$alias,$pais,$region,$provincia,$distrito)
{
if ($id_usuario==0)
{
    $row=  $this->_table->createRow();
    
}else
{
    $row=  $this->_table->find($id_usuario)->current();
}
$fechaActual= new Zend_Date();
$fechaActual->setTimezone('America/Lima');

$row->nombre=$nombre;
$row->apellido=$apellido;
$row->password=$password;
$row->rol=$rol;
$row->fecha=$fechaActual->get("Y-M-d H:m:s");
$row->alias=$alias;
$row->pais=$pais;
$row->region=$region;
$row->provincia=$provincia;
$row->distrito=$distrito;

$row->save();

}
}

