<?php

class Bootstrap extends Zend_Application_Bootstrap_Bootstrap
{

 protected function _initRouter(){
 $front= Zend_Controller_Front::getInstance();
 $router= $front->getRouter();
//instanciamos  zend controller router
$route = new Zend_Controller_Router_Route(
    'ver/:n',
        array(

        'controller' => 'ver',
        'action'     => 'index'
    )
);

//mandamos el nuevo ruter
$router->addRoute('ver', $route);
}
}

