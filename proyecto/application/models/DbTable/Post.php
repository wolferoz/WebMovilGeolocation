<?php

class Application_Model_DbTable_Post extends Zend_Db_Table_Abstract
{

    protected $_name = 'post';


public function Distrito($distrito)
{
    $db=$this->getAdapter();
    $select=$db->select();
    $select->from(array('a'=>$this->_name));
    $select->where('a.distrito=?',$distrito);
    $select->order(array('a.id_post DESC'));
    $select->where('a.permiso=?','1');
    return $db->fetchAll($select);
}
public function Provincia($provincia)
{
    $db=$this->getAdapter();
    $select=$db->select();
    $select->from(array('a'=>$this->_name));
    $select->where('a.provincia=?',$provincia);
    $select->order(array('a.id_post DESC'));
    $select->where('a.permiso=?','1');
    return $db->fetchAll($select);
}
public function Region($region)
{
    $db=$this->getAdapter();
    $select=$db->select();
    $select->from(array('a'=>$this->_name));
    $select->where('a.region=?',$region);
    $select->order(array('a.id_post DESC'));
    $select->where('a.permiso=?','1');
    return $db->fetchAll($select);
}
public function Pais($pais)
{
    $db=$this->getAdapter();
    $select=$db->select();
    $select->from(array('a'=>$this->_name));
    $select->where('a.pais=?',$pais);
    $select->order(array('a.id_post DESC'));
    $select->where('a.permiso=?','1');
    return $db->fetchAll($select);
}
public function Id($Id)
{
    $db=$this->getAdapter();
    $select=$db->select();
    $select->from(array('a'=>$this->_name));
    $select->where('a.id_post=?',$Id);
    return $db->fetchAll($select);
}
public function ultimos10()
{
    $db=$this->getAdapter();
    $select=$db->select();
    $select->from(array('a'=>$this->_name));
    $select->order(array('a.id_post DESC'));
    $select->where('a.permiso=?','1');
    $select->limit(10);
    return $db->fetchAll($select);
}
public function ultimos()
{
    $db=$this->getAdapter();
    $select=$db->select();
    $select->from(array('a'=>$this->_name));
    $select->order(array('a.id_post DESC'));
    $select->limit(10);
    return $db->fetchAll($select);
}
public function Rand()
{
    $db=$this->getAdapter();
    $select=$db->select();
    $select->from(array('a'=>$this->_name));
    $select->order('RAND()');
    $select->limit(2);
    return $db->fetchAll($select);
}
}