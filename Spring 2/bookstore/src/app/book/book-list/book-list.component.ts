import {Component, OnInit} from '@angular/core';
import {Book} from '../../model/book';
import {Category} from '../../model/category';
import {BookService} from '../book.service';
import {ToastrService} from 'ngx-toastr';
import {Title} from '@angular/platform-browser';
import {TokenStorageService} from '../../security/token-storage.service';
import {ShareService} from '../../security/share.service';
import {ActivatedRoute} from '@angular/router';
import {DataService} from '../data.service';
import Swal from 'sweetalert2';


@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.css']
})
export class BookListComponent implements OnInit {
  books: Book[] = [];
  id: number;
  number: number;
  indexPagination = 0;
  totalPage: Array<number>;
  previousPageStyle = 'inline-block';
  nextPageStyle = 'inline-block';
  totalElements = 0;
  pageSize: number;
  displayPagination = 'inline-block';
  numberOfElement = 0;
  keyword = '';
  nameDelete: string;
  idDelete: number;

  username: string;
  currentUser: string;
  role: string;
  isLoggedIn = false;
  categories: Category[] = [];
  cartList: any = this.bookService.getCarts();


  constructor(private bookService: BookService,
              private toastrService: ToastrService,
              private title: Title,
              private tokenStorageService: TokenStorageService,
              private shareService: ShareService,
              private activatedRoute: ActivatedRoute,
              private data: DataService) {
    this.title.setTitle('Tất cả sách');
    // this.data.getData.subscribe((result: any) => {
    //   this.keyword = result.keyword;
    // })
    this.shareService.getClickEvent().subscribe(() => {
      this.loadHeader();
    });
  }

  ngOnInit(): void {
    this.getAllCategory();
    this.loadHeader();
    this.getAll();
  }

  getAllCategory(): void {
    this.bookService.findAllCategory().subscribe(categories => {
      this.categories = categories;
    });
  }

  loadHeader(): void {
    if (this.tokenStorageService.getToken()) {
      this.currentUser = this.tokenStorageService.getUser().username;
      this.role = this.tokenStorageService.getUser().roles[0];
      this.username = this.tokenStorageService.getUser().username;
    }
    this.isLoggedIn = this.username != null;
  }

  getAll(): void {
    this.bookService.findAll(this.indexPagination, this.keyword, this.pageSize).subscribe((result?: any) => {
      if (result === null) {
        this.totalPage = new Array(0);
        this.books = [];
        this.displayPagination = 'none';
      } else {
        this.number = result?.number;
        this.pageSize = result?.size;
        this.numberOfElement = result?.numberOfElements;
        this.books = result.content;
        this.totalElements = result?.totalElements;
        this.totalPage = new Array(result?.totalPages);
      }
      this.checkPreviousAndNext();
    });
  }

  openDelete(book: Book) {
    this.nameDelete = book.name;
    this.idDelete = book.id;
  }

  delete(idDelete: number) {
    this.bookService.delete(idDelete).subscribe(() => {
      this.ngOnInit();
      Swal.fire('Thông báo', 'Xóa thành công', 'success');
    });
  }

  previousPage(event: any) {
    event.preventDefault();
    this.indexPagination--;
    this.ngOnInit();
  }

  nextPage(event: any) {
    event.preventDefault();
    this.indexPagination++;
    this.ngOnInit();
  }

  checkPreviousAndNext() {
    if (this.indexPagination === 0) {
      this.previousPageStyle = 'none';
    } else if (this.indexPagination !== 0) {
      this.previousPageStyle = 'inline-block';
    }
    if (this.indexPagination < (this.totalPage.length - 1)) {
      this.nextPageStyle = 'inline-block';
    } else if (this.indexPagination === (this.totalPage.length - 1) || this.indexPagination > (this.totalPage.length - 1)) {
      this.nextPageStyle = 'none';
    }
  }

  changePageSize(event: any) {
    switch (event.target.value) {
      case '6' :
        this.pageSize = 6;
        this.indexPagination = 0;
        this.ngOnInit();
        break;
      case '9' :
        this.pageSize = 9;
        this.indexPagination = 0;
        this.ngOnInit();
        break;
      case '12':
        this.pageSize = 12;
        this.indexPagination = 0;
        this.ngOnInit();
        break;

    }
  }

  addToCart(book: any) {
    const index = this.cartList.findIndex((item: any) => {
      return item.id === book.id;
    });

    if (index >= 0) {
      this.cartList[index].quantity += 1;
    } else {
      const cartItem: any = {
        id: book.id,
        name: book.name,
        price: book.price,
        quantity: 1,
        image: book.image
      };
      this.cartList.push(cartItem);
    }
    this.bookService.saveCarts(this.cartList);
    this.data.changeData({
      totalQuantity: this.bookService.getTotalCartQuantity()
    });
    Swal.fire('Thông báo', 'Thêm vào giỏ hàng thành công', 'success');
  }

  checkRegex(content: string): boolean {
    const format = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]+/;
    return format.test(content);
  }

  search() {
    if (this.checkRegex(this.keyword)) {
      this.indexPagination = 0;
      this.totalPage = new Array(0);
      this.books = [];
      this.displayPagination = 'none';
      this.checkPreviousAndNext();
    } else {
      this.indexPagination = 0;
      this.displayPagination = 'inline-block';
      this.getAll();
    }
  }
}
