# Camera-Streaming app
2018.05.13~08.10 차세대융합기술연구원(live Streaming app)

## Application Introduce
아프리카tv,youtube 등 온라인을 통해 개인이 직접 방송하는 1인방송의 시대를 반영하여 개발<br>
안드로이드의 카메라 모듈을 통해 실시간으로 영상을 촬영<br>
실시간 영상을 다수의 사용자들이 웹 또는 안드로이드 폰에서 재생<br>

<img src="https://user-images.githubusercontent.com/23578976/43948570-2455cb36-9cc6-11e8-9cd2-22320aba8e71.JPG" width="90%"></img>

## Architecture
<li>fyhertz/libstreaming의 오픈소스 기초<br>
<li>Android Server : 촬영 할 카메라의 raw데이터를 받아서 RTSP 서버 운영<br>
<li>FFMPGE library : RTSP->RTMP로 변환<br>
<li>Nignx Server : 동시접속에 특화된 스트리밍 웹서버 운영(nignx-rtmp-moudle 추가)<br>
<li>jw player : javascript기반 video player 오픈소스<br>
<li>Firebase : web hosting을 통해 도메인서버와 연결 (https://camera6-27edd.firebaseapp.com/)<br>


## Testing
<img src="https://user-images.githubusercontent.com/23578976/43948687-71c02470-9cc6-11e8-900b-1d9de46856a4.JPG" width="90%"></img>



