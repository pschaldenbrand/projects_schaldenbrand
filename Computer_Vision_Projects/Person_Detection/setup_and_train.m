%run('vlfeat-0.9.20/toolbox/vl_setup.m');

%do pos data first
posdir = 'person_detection_training_data/person_detection_training_data/pos/';
fnames = dir([posdir '*.png']);

posImgs = {};

for i = 1:length(fnames)
    fname = [posdir '/' fnames(i).name];
    im = imread(fname);
    
    posImgs{end + 1} = im;
end

dim = [160, 96];

%neg data. get two random spots from each neg image
negdir = 'person_detection_training_data/person_detection_training_data/neg/';
fnames = dir([negdir '*.png']);

negImgs = {};
for imgNum = 1:1:length(fnames)
    fname = [negdir '/' fnames(imgNum).name];
    im = imread(fname);
    
    for i = 1:1:3
        x = randi(size(im,2) - dim(2));
        y = randi(size(im,1) - dim(1));
        
        crop = im(y:y+dim(1)-1, x:x+dim(2)-1, : );
        negImgs{end+1} = crop;
    end
end
negImgs = negImgs(1 : size(posImgs,2) );

%train the model
posHOG = {};
negHOG = {};
for i = 1:1:size(posImgs,2)
    pIm = posImgs{i};
    nIm = negImgs{i};
   
    phog = [];
    nhog = [];
    for x = 1:8:size(pIm,2)
        for y = 1:8:size(pIm,1)
            im = pIm(y:y+7,x:x+7,:);
            v =  vl_hog(single(im),8);
            phog =[phog,v(:)'];
            
            im = nIm(y:y+7,x:x+7,:);
            v =  vl_hog(single(im),8);
            nhog =[nhog,v(:)'];
        end
    end
    posHOG{end+1} = phog;
    negHOG{end+1} = nhog;
end
    
x = [];
y = {};
for i = 1:1:size(posHOG,2)
    x(end+1,:) = posHOG{i};
    y{end+1} = 'person';
end
for i = 1:1:size(negHOG,2)
    x(end+1,:) = negHOG{i};
    y{end+1} = 'noperson';
end

model = fitcsvm(x,y);

save('model.mat','model');
    
    
    
