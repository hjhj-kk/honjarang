import Article from '@/components/Board/Article';
import { useNavigate } from 'react-router-dom';
import React, { Fragment, useEffect, useState } from 'react';
import axios from 'axios';
import Pagination from 'react-js-pagination';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { useCallback } from 'react';
import { activetabStyles } from '@/components/MyPage/MypageCss';
import { API } from '@/apis/config';

export default function AricleList() {
  const token = localStorage.getItem('access_token');
  const headers = { Authorization: `Bearer ${token}` };
  const [articles, setArticles] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(1);
  const [keyword, setKeyworkd] = useState('');
  const handleKeyword = (e) => {
    setKeyworkd(e.target.value);
  };
  const fetchArticles = useCallback(() => {
    axios
      .get(`${API.POST}`, {
        params: { page: currentPage, keyword: keyword },
        headers,
      })
      .then((res) => {
        console.log(res.data);
        setArticles(res.data);
      })
      .catch((err) => console.log(err));
  }, [currentPage, keyword]);

  const search = (e) => {
    e.preventDefault();
    if (keyword) {
      fetchArticles();
    }
  };
  useEffect(() => {
    axios
      .get(`${API.POST}/page`, {
        params: { size: 15, keyword: keyword },
        headers,
      })
      .then((res) => {
        setPageSize(res.data);
      })
      .catch((err) => {
        console.log(err);
      });
  }, [keyword]);

  useEffect(() => {
    fetchArticles();
  }, [currentPage]);

  const navigate = useNavigate();
  const handleButton = () => {
    navigate('/board/articlecreate');
  };
  const setPage = (error) => {
    setCurrentPage(error);
  };
  const MemorizedArticle = React.memo(Article);

  return (
    <div>
      {articles.length > 0 ? (
        <div className="w-3/5 mx-auto">
          <div className="text-xl mb-5 font-bold w-32" style={activetabStyles}>
            자유게시판
          </div>
          <div className="flex mb-5">
            <div className="w-3/6 font-bold text-center">제목</div>
            <div className="w-1/6 font-bold">작성자</div>
            <div className="w-1/6 font-bold">작성일</div>
            <div className="w-1/6 font-bold">조회수</div>
          </div>
          <hr />
          <div className="space-y-4 mt-1">
            {articles?.map((article) => (
              <div
                key={article.id}
                onClick={() => {
                  navigate(`/board/article/${article.id}`);
                }}
                className="cursor-pointer"
              >
                <MemorizedArticle article={article} />
                <hr />
              </div>
            ))}
          </div>
        </div>
      ) : (
        <div className="text-center text-lg">작성된 게시글이 없습니다</div>
      )}
      <footer className="w-3/5 mx-auto">
        <div className="flex justify-between mt-5">
          <form action="" className="space-x-2" onSubmit={search}>
            <input
              className="border border-gray2 focus:outline-main2 h-10 p-2"
              type="text"
              placeholder="검색어"
              onChange={handleKeyword}
            />
            <button>
              <FontAwesomeIcon
                icon={faMagnifyingGlass}
                style={{ color: '#008b28' }}
              />
            </button>
          </form>
          <button className="main1-full-button w-24" onClick={handleButton}>
            작성하기
          </button>
        </div>
        {articles.length > 0 ? (
          <Pagination
            activePage={currentPage}
            itemsCountPerPage={15}
            totalItemsCount={15 * pageSize}
            pageRangeDisplayed={10}
            prevPageText={'<'}
            nextPageText={'>'}
            onChange={setPage}
          />
        ) : null}
      </footer>
    </div>
  );
}
