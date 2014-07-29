<?php

class Application_Model_Usuario
{

 private $_table;
 function __construct(){
    $this->_table=new Application_Model_DbTable_Usuario();

}
public function guardar($id_usuario,$nombre,$apellido,$password,$rol,$fecha,$alias)
{
if ($id_usuario==0)
{
    $row=  $this->_table->createRow();
    
}else
{
    $row=  $this->_table->find($id_usuario)->current();
}
//$row->id_personal=$persona->getId();
//date_default_timezone_set('America/Lima');
$row->nombre=$nombre;
$row->apellido=$apellido;
$row->password=$password;
$row->rol=$rol;
$row->fecha=$fecha;
$row->alias=$alias;
$row->save();

}
}

