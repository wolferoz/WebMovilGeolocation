<?php

class Application_Model_DbTable_Provincias extends Zend_Db_Table_Abstract
{

    protected $_name = 'provincias';


public function All($Id)
{
    $db=$this->getAdapter();
    $select=$db->select();
    $select->from(array('a'=>$this->_name));
    $select->where('a.id_regiones=?',$Id);
    return $db->fetchAll($select);
}
//para cuando el usuario nos de su ubicacion
//obtener ip de la provincia
public function provincia_nombre($nombre)
{
    $db=$this->getAdapter();
    $select=$db->select();
    $select->from(array('a'=>$this->_name));
    $select->where('a.nombre=?',$nombre);
    return $db->fetchAll($select);
}
}