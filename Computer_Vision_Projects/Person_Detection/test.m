clear; close all;
load('model.mat');
dim = [160, 96];

picdir = 'test_images/';
fnames = dir([picdir '*.png']);

inc = 10;

imgPredicts = [];
numPredicts = 1;
goodImages = [ 92 80 69 59 54 44 32 26 18 3 ];

for imgNum = 1:1:length(fnames)
    fname = [picdir '/' fnames(imgNum).name];
    im = imread(fname);
    im = imresize(im,.4);
    
    imgPredicts(end+1) = 0;
    
    for x = 1:inc:size(im,2)
        if x+96 > size(im,2)
            continue;
        end
        for y = 1:inc:size(im,1)
            if y+160 > size(im,1)
                continue;
            end
            
            crop = im(y:y+dim(1)-1, x:x+dim(2)-1, : );
            
            %HOG
            hog = [];
            for x1 = 1:8:size(crop,2)
                for y1 = 1:8:size(crop,1)
                    lilim = crop(y1:y1+7,x1:x1+7,:);
                    v =  vl_hog(single(lilim),8);
                    hog =[hog,v(:)'];
                end
            end
            
            label = predict(model, hog);
            if strcmp(label{1},'person')
                %figure; imshow(crop);
                imgPredicts(imgNum) = imgPredicts(imgNum)+ 1;
                %if( size(find(goodImages==numPredicts),2) ~= 0)
                %    imwrite(crop,strcat('output_images/',num2str(numPredicts),'.jpg'));
                %end
                numPredicts = numPredicts + 1;
                
            end
        end
    end
end
    
    
    
    