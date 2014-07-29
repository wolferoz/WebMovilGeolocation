<?php

class Application_Model_DbTable_Usuario extends Zend_Db_Table_Abstract
{

    public $_name = 'usuario';

public function User_Dato($id_user){
    $db=$this->getAdapter();
    $select=$db->select();
    $select->from(array('a'=>$this->_name));
    $select->where('a.id_usuario=?',$id_user);
    
    return $db->fetchAll($select);
}

public function User_Id($id){
    $db=$this->getAdapter();
    $select=$db->select();
    $select->from(array('a'=>$this->_name));
    $select->where('a.id_usuario=?',$id);
    return $db->fetchAll($select);
}

public function Todo(){
    $db=$this->getAdapter();
    $select=$db->select();
    $select->from(array('a'=>$this->_name));
    return $db->fetchAll($select);
}
public function Ultimos(){
    $db=$this->getAdapter();
    $select=$db->select();
    $select->from(array('a'=>$this->_name));
    $select->limit(10);
    return $db->fetchAll($select);
}
public function Existe_alias($alias){
    $db=$this->getAdapter();
    $select=$db->select();
    $select->from(array('a'=>$this->_name));
    $select->where('a.alias=?',$alias);
    return $db->fetchAll($select);
}
}

