package com.hammerandtongues.online.hntonline;

        /**
 * Created by Calvin on 06 Jun,2016.
 */

public class CartManagement {

    // private variables
    int _CartID;
    String _dateCreated;
    int _CartStatusID;
    int _CartProductID;
    String _productDatecreated;
    String _ImgURL;
    int _quantity;
    Double _unitprice;
    Double _totalprice;
    Double _subTotal;
    Double _vat;
    String _product_name;
    int _Cart_Items_Count;
    Double _Cart_Items_Value;
            int _seller;

    // private variables for favorite items

    int _type_id;
    String _type;
    String _value_type;

    //private variables for Delivery

    int _Delivery_status;
    String _Address_1;
    String _Address_2;
    String _Address_3;
    String _Address_4;
    String _City;


    // End private variables

    // Lets have an empty constructor here!!

    public CartManagement()
    {

    }
    //favourites
    public CartManagement(int type_id, String type , String value_type)
    {
        this._type_id = type_id;
        this._type = type;
        this._value_type = value_type;
    }
    //delivery addresses
    public  CartManagement(int delivery_status, String address_1, String address_2, String address_3, String address_4, String city)
    {
        this._Delivery_status = delivery_status;
        this._Address_1 = address_1;
        this._Address_2= address_2;
        this._Address_3 = address_3;
        this._Address_4 = address_4;
        this._City = city;
    }
    //Cart **not being used
    public CartManagement(int cart_id, String date_created){
        this._CartID =cart_id;
        this._dateCreated = date_created;
    }

    // Constructor for saving the products into cart
    public CartManagement (int cart_id, String product_name, int quanity, int product_id, String imgurl, Double subtotal, Double unitprice, int seller)
    {
        this._CartID = cart_id;
        this._product_name = product_name;
        this._quantity = quanity;
        this._CartProductID = product_id;
        this._ImgURL = imgurl;
        this._subTotal = subtotal;
        this._unitprice = unitprice;
        this._seller = seller;
    }

    // End Empty constructor

    // Here we are gonna have public properties for our variables !!
    // Here we are doing public properties for favorites
    public int get_type_id(){return  _type_id;}
    public void set_type_id(int Type_ID){this._type_id =Type_ID;}
    public String get_type(){return _type;}
    public void set_type(String Type){this._type = Type;}
    public String get_value_type(){return _value_type;}
    public void set_value_type(String Value_Type){this._value_type=Value_Type;}

    public int get_Delivery_status(){return _Delivery_status;}
    public void set_Delivery_status(int Delivery_Status) {this._Delivery_status = Delivery_Status;}
    public String get_Address_1(){return _Address_1;}
    public void set_Address_1(String Address_1) {this._Address_1 = Address_1;}
    public String get_Address_2(){return _Address_2;}
    public void set_Address_2(String Address_2) {this._Address_2 = Address_2;}
    public String get_Address_3(){return _Address_3;}
    public void set_Address_3(String Address_3) {this._Address_3 = Address_3;}
    public String get_Address_4(){return _Address_4;}
    public void set_Address_4(String Address_4) {this._Address_4 = Address_4;}
    public String get_City(){return _City;}
    public void set_City(String City) {this._City = City;}


    public String get_ImgURL(){return _ImgURL;}
    public void set_ImgURL(String imgurl) {this._ImgURL = imgurl;}

    public Double get_SubTotal(){return _subTotal;}
    public void set_SubTotal(Double subtotal) {this._subTotal = subtotal;}


    public int get_CartID()
    {
        return this._CartID;
    }
    public  void set_CartID(int cartID){
        this._CartID=cartID;
    }
    public String get_dateCreated ()
    {
        return  _dateCreated;
    }
    public  void set_dateCreated(String dateCreated)
    {
        this._dateCreated = dateCreated;
    }
    public  int get_CartStatusID(){return this._CartStatusID;}
    public void set_CartStatusID(int cartStatusID){this._CartStatusID = cartStatusID;}
    public int get_CartProductID(){ return _CartProductID; }
    public  void set_CartProductID(int cartProductID){this._CartProductID = cartProductID;}
    public String get_productDatecreated(){
        return _productDatecreated;
    }
    public void set_productDatecreated(String productDatecreated){this._productDatecreated = productDatecreated;}
    public int  get_quantity(){return  _quantity;}
    public void set_quantity(int quantity){this._quantity = quantity;}
    public Double get_unitprice(){return  _unitprice;}
    public void set_unitprice(Double unitprice){this._unitprice = unitprice;}
    public  Double get_totalprice(){return this._totalprice;}
    public void set_totalprice(Double totalprice){this._totalprice = totalprice;}
    public int get_seller(){return _seller;}
            public void set_seller(int seller){this._seller = seller;}
            public Double get_vat(){return _vat;}
    public void set_vat(Double vat){this._vat = vat;}
    public  String get_product_name(){return  _product_name;}
    public  void set_product_name(String Product_name){this._product_name = Product_name;}

    public int get_Cart_Items_Count (){return _Cart_Items_Count;}
    public void set_Cart_Items_Count(int Items_count){this._Cart_Items_Count = Items_count;}

    public Double get_Cart_Items_Value (){return _Cart_Items_Value;}
    public void set_Cart_Items_Value(Double Items_Value){this._Cart_Items_Value = Items_Value;}


    // End public properties



}
